package com.johnwilliams.qq.tools.Chat;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import cn.bmob.v3.BmobObject;

@Entity(tableName = "chats")
public class Chat {
    @PrimaryKey(autoGenerate = true)
    public int chat_id;

    @NonNull
    @ColumnInfo(name = "stunum")
    public String student_number = "";

    @ColumnInfo(name = "name")
    public String name = "";

    @ColumnInfo(name = "time")
    public long time = 0L;

    @ColumnInfo(name = "unread")
    public int unread = 0;

    @ColumnInfo(name = "last_msg")
    public String last_msg = "";

    @Ignore
    public Chat(){

    }

    public Chat(String student_number, String name, long time, int unread, String last_msg){
        this.student_number = student_number;
        this.name = name;
        this.time = time;
        this.unread = unread;
        this.last_msg = last_msg;
    }

    public Chat(String stunum, String name){
        this.student_number = stunum;
        this.name = name;
    }

    public int getChat_id(){return chat_id;}
    public String getStudent_number(){return student_number;}
    public String getName(){return name;}
}


