package com.johnwilliams.qq.tools.Connection;

import android.os.AsyncTask;
import android.util.Log;

import java.net.*;
import java.io.*;
import java.util.Arrays;

//import cn.bmob.v3.util.AppUtils;

public class ConnectionTool {
//    private static String host;
//    private static int port;
    public static String ServerIP = "166.111.140.14";
    public static String ServerPort = "8000";
    public String LocalPort = "50000";
    public Socket socket = null;
    protected  BufferedReader inFromServer = null;
    protected BufferedWriter outToServer = null;
    protected char[] cbuf = new char[50];
//    protected static AsyncTask<String, Void, Void> init =  new AsyncTask<String, Void, Void>() {
//        @Override
//        protected Void doInBackground(String... strings) {
//            try{
//                if (socket != null && socket.isConnected()){
//                    return null;
//                }
//                socket = new Socket();
//                socket.setReuseAddress(true);//TODO: important!
////                    socket.bind(new InetSocketAddress(Integer.parseInt(localPort)));
//                socket.connect(new InetSocketAddress(strings[0], Integer.parseInt(strings[1])));
//                outToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            }
//            catch (Exception e)
//            {
//                Log.v("ErrorBalaBala", e.getMessage());
//            }
//            return null;
//        }
//    };

//    protected static AsyncTask<String, Void, String> send = new AsyncTask<String, Void, String>() {
//        @Override
//        protected String doInBackground(String... strings) {
//            try{
//                outToServer.write(strings[0]);
//                outToServer.flush();
//                Arrays.fill(cbuf, '\0');
//                inFromServer.read(cbuf);
//            }
//            catch (Exception e){
//                Log.v("Error", e.getMessage());
//            }
//            int i = 0;
//            while(cbuf[i++] != '\0');
//            return String.valueOf(cbuf, 0, --i);
//        }
//    };

    public void ConnectionInit(String host, String port, final String localPort) throws Exception{
//        init.execute(host, port);
        AsyncTask<String, Void, Void> init = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                try{
                    if (socket != null && socket.isConnected()){
                        return null;
                    }
                    socket = new Socket();
                    socket.setReuseAddress(true);//TODO: important!
//                    socket.bind(new InetSocketAddress(Integer.parseInt(localPort)));
                    socket.connect(new InetSocketAddress(strings[0], Integer.parseInt(strings[1])));
                    outToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
                catch (Exception e)
                {
                    Log.v("ErrorBalaBala", e.getMessage());
                }
                return null;
            }
        };
//        init.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, host, port);
        init.execute(host, port);
    }

    public String getIp(String student_number) throws Exception{
        return SendCommand("q" + student_number);
    }

    public String Login(String student_number, String password) throws Exception{
        String reply = SendCommand(student_number + "_" +
                password);
//        return reply.equals("lol");
        return reply;
    }

    public boolean Logout(String student_number) throws Exception{
        String reply = SendCommand("logout" + student_number);
        return reply.equals("loo");
    }

    public String SendCommand(String cmd) throws Exception {
        AsyncTask<String, Void, String> send = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try{
                    Arrays.fill(cbuf, '\0');
                    outToServer.write(strings[0]);
                    outToServer.flush();
                    inFromServer.read(cbuf);
                }
                catch (Exception e){
                    Log.v("Error", e.getMessage());
                }
                int i = 0;
                while(cbuf[i++] != '\0');
                return String.valueOf(cbuf, 0, --i);
            }
        };
        return send.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cmd).get();//TODO, key issue
//        String result = send.execute(cmd).get();
//        return result;
    }

    public void ConnectionEnd() throws Exception {
        if (socket == null)
            return;
        socket.close();
        socket = null;
        outToServer.close();
        inFromServer.close();
        System.out.println("Bye");
    }
}
