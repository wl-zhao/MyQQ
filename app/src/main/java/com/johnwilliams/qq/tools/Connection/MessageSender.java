package com.johnwilliams.qq.tools.Connection;

import android.icu.util.Output;
import android.net.Uri;
import android.util.Log;

import com.johnwilliams.qq.Activities.LoginActivity;
import com.johnwilliams.qq.tools.Message.ChatMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

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

    public boolean SendFile(String localPath, ChatMessage message) throws Exception{
        FileInputStream fileInput = new FileInputStream(localPath);
        int size = -1;
        byte [] buffer = new byte[1024];
        while ((size = fileInput.read(buffer, 0, 1024)) != -1){
            //outputData.write(buffer, 0, size);
        }
        //outputData.close();
        fileInput.close();
        return false;
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
