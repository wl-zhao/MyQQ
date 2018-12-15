package com.johnwilliams.qq.tools.Message;

import java.util.Locale;

import cn.bmob.v3.BmobObject;

public class ChatMessage extends BmobObject {

    public enum MSG_TYPE{
        CMD(0), TEXT(1), IMG(2), FILE(3), AUDIO(4);
        private final int value;
        MSG_TYPE(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }

    public enum MSG_STATUS{
        SENDING(0), SENT(1), FAILED(2);
        private final int value;
        MSG_STATUS(int value){
            this.value = value;
        }
        public int getValue(){
            return  value;
        }
    }

    final public static int MAX_CONTENT_LENGTH = 500;
    final public static int STUNUM_LENGTH = 10;

    public static String default_num = "0000000000";
    private String from = default_num;
    private String to = default_num;
    private String content = "";
    private Long time = 0L;
    private MSG_TYPE type = MSG_TYPE.TEXT;
    private MSG_STATUS status = MSG_STATUS.SENDING;



    public ChatMessage(){

    }

    public ChatMessage(String str){
        fromString(str);
    }

    public ChatMessage(String from, String to, String content, Long time, MSG_TYPE type, MSG_STATUS status){
        this.from = from;
        this.to = to;
        this.content = content;
        this.time = time;
        this.type = type;
        this.status = status;
    }

    @Override
    public String toString(){
        String bytes = "";
        // from and to
        bytes += from + to;
        // type
        bytes += String.valueOf(type.getValue());
        // time
        String time_string = String.valueOf(time);

        bytes += String.format(Locale.US, "%02d", time_string.length());
        bytes += time_string;

        // content
        bytes += content;

        // length
        bytes = String.format(Locale.US, "%03d", bytes.length() + 3) + bytes;
        return bytes;
    }

    public void fromString(String s){
        int cursor = 0;
        int content_length = Integer.parseInt(s.substring(cursor, cursor += 3));
        from = s.substring(cursor, cursor += STUNUM_LENGTH);//TODO: maybe wrong
        to = s.substring(cursor, cursor += STUNUM_LENGTH);
        type = MSG_TYPE.values()[Integer.parseInt(s.substring(cursor, cursor += 1))];
        int time_length = Integer.parseInt(s.substring(cursor, cursor += 2));
        time = Long.parseLong(s.substring(cursor, cursor += time_length));
        content = s.substring(cursor, s.length());
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public MSG_TYPE getType() {
        return type;
    }

    public void setType(MSG_TYPE type) {
        this.type = type;
    }

    public MSG_STATUS getStatus(){
        return status;
    }
    public void setStatus(MSG_STATUS status){
        this.status = status;
    }
}
