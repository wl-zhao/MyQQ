package com.johnwilliams.qq.tools.Connection;

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

    public ServerWorkerRunnable(Socket clientSocket){
        initWithSocket(clientSocket);
    }

    public void initWithSocket(Socket clientSocket){
        this.clientSocket = clientSocket;
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
                results.add(chatMessage);

                // Send message using handler
                if (chatMessageHandler == null) {
                    chatMessageHandler = ChatActivity.chatMessageHandler;
                }
                if (mainMessageHandler == null){
                    mainMessageHandler = MainActivity.mainMessageHandler;
                }
                if (chatMessageHandler != null)
                    chatMessageHandler.sendEmptyMessage(Constant.NEW_MESSAGE);
                if (mainMessageHandler != null)
                    mainMessageHandler.sendEmptyMessage(Constant.NEW_MESSAGE);

                if (chatMessage.getType() == ChatMessage.MSG_TYPE.CMD &&
                        chatMessage.getContent().equals("BYE")){
                    break;
                }
            }
            input.close();
            output.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized List<ChatMessage> getResults(){
        List<ChatMessage> tmp = new ArrayList<>(results);
        results.clear();
        return tmp;
    }
}
