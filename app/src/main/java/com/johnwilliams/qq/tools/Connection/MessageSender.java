package com.johnwilliams.qq.tools.Connection;

import android.icu.util.Output;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.johnwilliams.qq.Activities.ChatActivity;
import com.johnwilliams.qq.Activities.LoginActivity;
import com.johnwilliams.qq.tools.Message.ChatMessage;
import com.johnwilliams.qq.tools.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

public class MessageSender extends ConnectionTool {

    public String my_stunum;
    public String friend_stunum;
    private String friend_ip;
    private String LocalPort = "50001";

    public void DataInit(String my_stunum, String friend_stunum){
        this.my_stunum = my_stunum;
        this.friend_stunum = friend_stunum;
    }

    public void ConnectionInit() throws Exception{
        friend_ip = LoginActivity.connectionTool.getIp(friend_stunum);
        if (friend_ip.equals("n")){
            throw new Exception("好友不在线");
        }
        ConnectionInit(friend_ip, MessageReceiver.ServerPort, LocalPort);
    }

    public static void Ping(){

    }

    public boolean SendMessage(ChatMessage message){
        try {
            String reply = SendCommand(message.toString());
            if (reply.equals("ACK")){
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e){
            Log.v("SendError", e.getMessage());
        }
        return false;
    }

    public void SendFile(String localPath, ChatMessage fileMessage) throws Exception{
        final AsyncTask<String, Void, Void> sendFile = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                try{
                    FileInputStream fileInput = new FileInputStream(strings[0]);
                    ChatMessage fileMessage = new ChatMessage(strings[1]);
                    OutputStream outputStream = socket.getOutputStream();
                    int size;
                    Long sent_size = 0L;
                    String[] splitted = fileMessage.getContent().split("\\?");
                    fileMessage.setContent(splitted[0]);
                    fileMessage.setFile_length(Long.parseLong(splitted[1]));
                    if (fileMessage.getType() == ChatMessage.MSG_TYPE.AUDIO) {
                        fileMessage.setAudio_length(Integer.parseInt(splitted[2]));
                    }
                    byte [] buffer = new byte[1024];
                    Message msg;
                    while ((size = fileInput.read(buffer, 0, 1024)) != -1){
                        outputStream.write(buffer, 0, size);
                        sent_size += size;
                        msg = new Message();
                        msg.what = Utils.UPDATE_PROGRESS;
                        fileMessage.setProgress((int)(100 * sent_size / fileMessage.getFile_length()));
                        msg.obj = fileMessage;
                        ChatActivity.chatMessageHandler.sendMessage(msg);
                    }
                    msg = new Message();
                    msg.what = Utils.UPDATE_PROGRESS;
                    fileMessage.setProgress(200);// done
                    fileMessage.setStatus(ChatMessage.MSG_STATUS.SENT);
                    msg.obj = fileMessage;
                    ChatActivity.chatMessageHandler.sendMessage(msg);
                    outputStream.close();
                    fileInput.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        sendFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, localPath, fileMessage.toString());
    }

    @Override
    public void ConnectionEnd() throws Exception{
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent("BYE");
        chatMessage.setType(ChatMessage.MSG_TYPE.CMD);
        SendMessage(chatMessage);
        super.ConnectionEnd();
    }
}
