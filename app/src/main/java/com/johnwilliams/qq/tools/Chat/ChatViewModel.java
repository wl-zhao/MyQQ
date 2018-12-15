package com.johnwilliams.qq.tools.Chat;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private ChatRepository mRepository;
    private LiveData<List<Chat>> mAllChats;

    public ChatViewModel(Application application){
        super(application);
        mRepository = new ChatRepository(application);
        mAllChats = mRepository.getAllChats();
    }

    public LiveData<List<Chat>> getAllChats(){
        return mAllChats;
    }

    public void insert(Chat chat){
        if (Contains(chat)){
            return;
        }
        mRepository.insert(chat);
    }

    public void clear(){
        mRepository.clear();
    }

    public void removeAt(int position){
        mRepository.removeAt(position);
    }

    public boolean Contains(Chat newChat){
        try {
            List<Chat> chats = mAllChats.getValue();
            for (Chat chat : chats){
                if (chat.student_number.equals(newChat.student_number)){
                    return true;
                }
            }
        } catch (Exception e){
            return false;
        }
        return false;
    }
}
