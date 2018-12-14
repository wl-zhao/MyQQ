package com.johnwilliams.qq.tools.Connection;

import android.util.Log;

import com.johnwilliams.qq.Activities.LoginActivity;
import com.johnwilliams.qq.tools.Message.ChatMessage;

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

    @Override
    public void ConnectionEnd() throws Exception{
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent("BYE");
        chatMessage.setType(ChatMessage.MSG_TYPE.CMD);
        SendMessage(chatMessage);
        super.ConnectionEnd();
    }
}
