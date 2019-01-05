package com.johnwilliams.qq.tools.Connection;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.johnwilliams.qq.tools.Utils;

import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//import cn.bmob.v3.util.AppUtils;

public class ConnectionTool {
//    private static String host;
//    private static int port;
    public static String ServerIP = "166.111.140.14";
    public static String ServerPort = "8000";
    public String LocalPort = "50000";
    public Socket socket = null;
    public String mIp, mPort;
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

    public void ConnectionInit(final String host, final String port, final String localPort) throws Exception{
//        init.execute(host, port);
        mIp = host;
        mPort = port;
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (socket != null && socket.isConnected()){
                        return;
                    }
                    socket = new Socket();
                    socket.setSoTimeout(2000);
                    socket.connect(new InetSocketAddress(host, Integer.parseInt(port)));
                    outToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
                catch (Exception e)
                {
                    Log.v("ErrorBalaBala", e.getMessage());
                }
            }
        });
        connectionThread.start();
//        AsyncTask<String, Void, Void> init = new AsyncTask<String, Void, Void>() {
//            @Override
//            protected Void doInBackground(String... strings) {
//                try{
//                    if (socket != null && socket.isConnected()){
//                        return null;
//                    }
//                    socket = new Socket();
//                    socket.setSoTimeout(2000);
////                    socket.setReuseAddress(true);//TODO: important!
////                    socket.bind(new InetSocketAddress(Integer.parseInt(localPort)));
//                    socket.connect(new InetSocketAddress(strings[0], Integer.parseInt(strings[1])));
//                    outToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                    inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                }
//                catch (Exception e)
//                {
//                    Log.v("ErrorBalaBala", e.getMessage());
//                }
//                return null;
//            }
//        };
//        init.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, host, port);
//        init.execute(host, port);
    }

    // get Ip of student / students with student_number
    public String getIp(String student_number) throws Exception{
        String[] student_numbers = student_number.split(",");
        boolean all_offline = true;
        StringBuilder ips = new StringBuilder();
        String reply;
        for (String s : student_numbers) {
            reply = SendCommand("q" + s);
            if (reply.matches(Utils.IPV4_REGEX)) {
                all_offline = false;
                ips.append(reply);
                ips.append(",");
            }
        }
        if (all_offline) {
            return "n";
        } else {
            return ips.toString().substring(0, ips.length() - 1);
        }
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

    public String SendCommand(final String cmd) throws Exception {
        final AsyncTask<String, Void, String> send = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try{
                    if (inFromServer == null){
                        inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    }
                    Arrays.fill(cbuf, '\0');
                    if (outToServer == null) {
                        outToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    }
                    outToServer.write(strings[0]);
                    outToServer.flush();
                    inFromServer.read(cbuf);
                }
                catch (Exception e){
                    Log.v("Error", e.getMessage());
                    if (e.getMessage().contains("EBADF")) {
                        try {
                            socket = null;
                            ConnectionInit(mIp, mPort, "");
//                            doInBackground(strings);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }
                    return "Error";
//                    inFromServer.close();
//                    inFromServer = null;
                }
                int i = 0;
                while(cbuf[i++] != '\0');
                return String.valueOf(cbuf, 0, --i);
            }
        };
        return send.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cmd).get();//TODO, key issue

//        return reply;
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

    @Override
    public void finalize(){
        try {
            socket.close();
        } catch (Exception e){

        }
    }

    private class RetString implements Callable<String> {
        String mCmd;
        RetString(String cmd) {
            mCmd = cmd;
        }

        @Override
        public String call(){
            try{
                if (inFromServer == null){
                    inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
                Arrays.fill(cbuf, '\0');
                if (outToServer == null) {
                    outToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                }
                outToServer.write(mCmd);
                outToServer.flush();
                inFromServer.read(cbuf);
            }
            catch (Exception e){
                Log.v("Error", e.getMessage());
                if (e.getMessage().contains("EBADF")) {
                    try {
                        socket = null;
                        ConnectionInit(mIp, mPort, "");
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
                return "Error";
            }
            int i = 0;
            while(cbuf[i++] != '\0');
            return String.valueOf(cbuf, 0, --i);
        }
    }
}
