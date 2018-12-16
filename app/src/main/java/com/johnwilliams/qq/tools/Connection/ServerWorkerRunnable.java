package com.johnwilliams.qq.tools.Connection;

import android.os.Message;

import com.johnwilliams.qq.Activities.ChatActivity;
import com.johnwilliams.qq.Activities.MainActivity;
import com.johnwilliams.qq.tools.Constant;
import com.johnwilliams.qq.tools.Message.ChatMessage;
import java.net.Socket;
import java.util.List;
import java.io.*;
import java.util.*;

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
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            while(true){
                result = new StringBuilder();
                input.read(length_info);// read whole length of message
                if (isStopped){
                    break;
                }
                result.append(String.valueOf(length_info));
                int length = Integer.parseInt(result.toString());
                while (result.length() < length){
                    bytesRead = input.read(messageBuffer);
                    result.append(new String(messageBuffer, 0, bytesRead));
                    StringBuilder s;
                }
                output.write("ACK");
                output.flush();
                System.out.println(result);
                chatMessage = new ChatMessage(result.toString());

                if (chatMessage.getType() == ChatMessage.MSG_TYPE.CMD &&
                        chatMessage.getContent().equals("BYE")){
                    break;
                }
                results.add(chatMessage);

                // Send message using handler
                Message msg = new Message();
                msg.what = Constant.NEW_MESSAGE;
                msg.obj = chatMessage;
                if (ChatActivity.chatMessageHandler != null) {
                    ChatActivity.chatMessageHandler.sendMessage(msg);
                }

                msg = new Message();
                msg.what = Constant.NEW_MESSAGE;
                msg.obj = chatMessage;
                if (MainActivity.mainMessageHandler != null)
                    MainActivity.mainMessageHandler.sendMessage(msg);


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
