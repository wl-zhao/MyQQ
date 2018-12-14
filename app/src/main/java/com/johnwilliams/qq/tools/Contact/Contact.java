package com.johnwilliams.qq.tools.Contact;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    public int contact_id;

    @NonNull
    @ColumnInfo(name = "stunum")
    public String student_number;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "online")
    public boolean online;

    @Ignore
    public Contact(){

    }

    public Contact(String student_number, String name, boolean online){
        this.student_number = student_number;
        this.name = name;
        this.online = online;
    }

    @Ignore
    public Contact(String student_number){
        this.student_number = student_number;
    }
}


