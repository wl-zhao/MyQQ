package com.johnwilliams.qq.tools.Message;

import android.arch.persistence.room.util.StringUtil;

import java.io.File;
import java.util.Locale;

import cn.bmob.v3.BmobObject;

public class ChatMessage extends BmobObject {

    public enum MSG_TYPE{
        CMD(0), TEXT(1), EMO(2), IMG(3), FILE(4), AUDIO(5);
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
    private String from_stunum = default_num;
    private String to_stunum = default_num;
    private String content = "";

    private Long file_length = 0L;



    private int audio_length = 0;

    private int progress = 0;

    private Long time = 0L;
    private MSG_TYPE type = MSG_TYPE.TEXT;
    private MSG_STATUS status = MSG_STATUS.SENDING;



    public ChatMessage(){

    }

    public ChatMessage(String str){
        fromString(str);
    }

    public ChatMessage(String from_stunum, String to, String content, Long time, MSG_TYPE type, MSG_STATUS status){
        this.from_stunum = from_stunum;
        this.to_stunum = to;
        this.content = content;
        this.time = time;
        this.type = type;
        this.status = status;
    }

    @Override
    public String toString(){
        String bytes = "";
        // from_stunum and to_stunum
        bytes += from_stunum + to_stunum;
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
        int num_receivers = s.length() - s.replace(",", "").length() + 1;
        int content_length = Integer.parseInt(s.substring(cursor, cursor += 3));
        from_stunum = s.substring(cursor, cursor += STUNUM_LENGTH);//TODO: maybe wrong
        to_stunum = s.substring(cursor, cursor += STUNUM_LENGTH * num_receivers + num_receivers - 1);//take commas into account
        type = MSG_TYPE.values()[Integer.parseInt(s.substring(cursor, cursor += 1))];
        int time_length = Integer.parseInt(s.substring(cursor, cursor += 2));
        time = Long.parseLong(s.substring(cursor, cursor += time_length));
        content = s.substring(cursor, s.length());
    }

    public void addFileLength() {
        File file = new File(content);
        Long file_length = file.length();
        content = content + "?" + file_length.toString();
    }

    public boolean isFileType() {
        return type.getValue() >= 3;
    }

    public String getFrom_stunum() {
        return from_stunum;
    }

    public void setFrom_stunum(String from_stunum) {
        this.from_stunum = from_stunum;
    }


    public String getTo_stunum() {
        return to_stunum;
    }

    public void setTo_stunum(String to_stunum) {
        this.to_stunum = to_stunum;
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

    public Long getFile_length() {
        return file_length;
    }

    public void setFile_length(Long file_length) {
        this.file_length = file_length;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getAudio_length() {
        return audio_length;
    }

    public void setAudio_length(int audio_length) {
        this.audio_length = audio_length;
    }
}
