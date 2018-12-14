package com.johnwilliams.qq.tools.Chat;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ChatDao {

    @Query("SELECT * FROM chats")
    LiveData<List<Chat>> getAll();

    @Query("SELECT * FROM chats WHERE name LIKE :name LIMIT 1")
    Chat findByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chat chat);

    @Update
    void update(Chat chat);

    @Delete
    void delete(Chat chat);

    @Query("DELETE FROM chats")
    void deleteAll();
}
