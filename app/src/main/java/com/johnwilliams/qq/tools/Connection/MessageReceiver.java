package com.johnwilliams.qq.tools.Connection;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.johnwilliams.qq.tools.Message.ChatMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MessageReceiver implements Runnable{
    public List<ChatMessage> unread_msgs;
    public int unread_num = 0;

    public static String ServerPort = "50002";
    protected ServerSocket serverSocket = null;
    protected Socket clientSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    protected ServerWorkerRunnable serverWorkerRunnable = null;


    public MessageReceiver(){
        unread_msgs = new ArrayList<>();
    }

    public List<ChatMessage> readMsg(){
        List<ChatMessage> result = new ArrayList<>(unread_msgs);
        unread_msgs.clear();
        unread_num = 0;
        return result;
    }

    public ChatMessage get(int position){
        return unread_msgs.get(position);
    }

    @Override
    public void run(){
        isStopped = false;
        synchronized (this){
            this.runningThread = Thread.currentThread();
        }
        AsyncTask<Void, Void, Void> serverTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                openServerSocket();
                while(!isStopped()){
                    try{
                        if (serverSocket != null)
                            clientSocket = serverSocket.accept();
                    } catch (Exception e){
                        if (isStopped()){
                            Log.d("ConnectionError", "Server Stopped");
                            return null;
                        }
                        throw new RuntimeException("Error accepting", e);
                    }
                    if (serverWorkerRunnable == null){
                        serverWorkerRunnable = new ServerWorkerRunnable(clientSocket);
                    } else {
                        serverWorkerRunnable.initWithSocket(clientSocket);
                    }
                    new Thread(
                            serverWorkerRunnable
                    ).start();
                }
                Log.d("ConnectionFinish", "Server Stopped");
                return null;
            }
        };
        serverTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized boolean isStopped(){
        return isStopped;
    }

    public synchronized void stop(){
        isStopped = true;
        try {
            if (serverSocket != null){
                serverSocket.close();
                serverWorkerRunnable.stop();
                serverSocket = null;
            }
        } catch (IOException e){
            throw new RuntimeException("Fail to close Server", e);
        }
    }


    private void openServerSocket(){
        int port = Integer.parseInt(ServerPort);
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));
        } catch (IOException e){
            throw new RuntimeException("Cannot open port" + String.valueOf(port), e);
        }
    }

    public synchronized void receiveMsg(){
        List<ChatMessage> results = serverWorkerRunnable.getResults();
        unread_msgs.addAll(results);
        unread_num += results.size();
//        for (int i = 0; i < results.size(); ++i){
//            unread_msgs.add(results.get(i));
//            unread_num++;
//        }
    }
}
