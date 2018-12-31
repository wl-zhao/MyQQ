package com.johnwilliams.qq.tools.Connection;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Message;

import com.johnwilliams.qq.Activities.ChatActivity;
import com.johnwilliams.qq.Activities.MainActivity;
import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Utils;
import com.johnwilliams.qq.tools.Message.ChatMessage;

import java.net.Socket;
import java.util.List;
import java.io.*;
import java.util.*;

import androidx.annotation.MainThread;

public class ServerWorkerRunnable implements Runnable{
    protected Socket clientSocket;
    protected char[] messageBuffer = new char[1000];
    private volatile List<ChatMessage> results = new ArrayList<>();
    private ChatActivity.ChatMessageHandler chatMessageHandler;
    private MainActivity.MainMessageHandler mainMessageHandler;
    private boolean isStopped = false;

    public ServerWorkerRunnable(Socket clientSocket){
        initWithSocket(clientSocket);
    }

    public void initWithSocket(Socket clientSocket){
        this.clientSocket = clientSocket;
        isStopped = false;
    }

    @Override
    public void run(){
        try {
            StringBuilder result;
            int bytesRead;
            ChatMessage chatMessage;
            char[] length_info = new char[3];
            BufferedReader input;
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            while(true){
                result = new StringBuilder();
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                int read_result = input.read(length_info);// read whole length of message
                if (read_result == -1) {
                    continue;
                }
                if (isStopped){
                    break;
                }
                result.append(String.valueOf(length_info));
                int length = Integer.parseInt(result.toString());
                while (result.length() < length){
                    bytesRead = input.read(messageBuffer);
                    result.append(new String(messageBuffer, 0, bytesRead));
                }
                output.write("ACK");
                output.flush();
                System.out.println(result);
                chatMessage = new ChatMessage(result.toString());

                if (chatMessage.getType() == ChatMessage.MSG_TYPE.CMD &&
                        chatMessage.getContent().equals("BYE")){
                    break;
                }

                if (chatMessage.isFileType()){
                    String[] splited = chatMessage.getContent().split("\\?");
                    chatMessage.setContent(splited[0]);
                    chatMessage.setFile_length(Long.parseLong(splited[1]));
                    if (chatMessage.getType() == ChatMessage.MSG_TYPE.AUDIO) {
                        chatMessage.setAudio_length(Integer.parseInt(splited[2]));
                    }
                }

                results.add(chatMessage);

                // Send message using handler
                Message msg = new Message();
                msg.what = Utils.NEW_MESSAGE;
                msg.obj = chatMessage;
                if (ChatActivity.chatMessageHandler != null) {
                    ChatActivity.chatMessageHandler.sendMessage(msg);
                }

                msg = new Message();
                msg.what = Utils.NEW_MESSAGE;
                msg.obj = chatMessage;
                if (MainActivity.mainMessageHandler != null)
                    MainActivity.mainMessageHandler.sendMessage(msg);

                // if is file
                if (chatMessage.isFileType()){
                    InputStream inputStream = clientSocket.getInputStream();
                    String subdir = Utils.DEFAULT_PATH;
                    switch (chatMessage.getType()) {
                        case AUDIO:
                            subdir += Utils.AUDIO_SUBDIR;
                            break;
                        case IMG:
                            subdir += Utils.IMAGE_SUBDIR;
                            break;
                        case FILE:
                            subdir += Utils.FILE_SUBDIR;
                            break;
                    }

                    File dir = new File(Environment.getExternalStorageDirectory().getPath() + subdir);
                    if (!dir.exists()){
                        dir.mkdirs();
                    }
                    String savePath = Environment.getExternalStorageDirectory().getPath() + subdir
                            + Utils.convertFileName(chatMessage.getContent(), false);
                    FileOutputStream file = new FileOutputStream(savePath, false);
                    byte[] buffer = new byte[1024];
                    int size;
                    Long received_length = 0L;
                    while ((size = inputStream.read(buffer)) != -1){
                        file.write(buffer, 0, size);
                        received_length += size;
                        chatMessage.setProgress((int)(received_length * 100.0 / chatMessage.getFile_length()));
                        msg = new Message();
                        msg.what = Utils.UPDATE_PROGRESS;
                        msg.obj = chatMessage;
                        if (ChatActivity.chatMessageHandler != null){
                            ChatActivity.chatMessageHandler.sendMessage(msg);
                        }
                        if (received_length.equals(chatMessage.getFile_length())){
                            break;
                        }
                    }
                    file.close();
                    input.close();
                    msg = new Message();
                    msg.what = Utils.UPDATE_PROGRESS;
                    chatMessage.setProgress(200);// finished
                    msg.obj = chatMessage;
                    if (ChatActivity.chatMessageHandler != null){
                        ChatActivity.chatMessageHandler.sendMessage(msg);
                    }
                    break;
                }
            }
            input.close();
            output.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void stop(){
        isStopped = true;
    }

    public synchronized List<ChatMessage> getResults(){
        List<ChatMessage> tmp = new ArrayList<>(results);
        results.clear();
        return tmp;
    }
}
