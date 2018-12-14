package com.johnwilliams.qq.tools.Chat;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ChatRepository {
    private ChatDao mChatDao;
    private LiveData<List<Chat>> mAllChats;

    ChatRepository(Application application){
        ChatDatabase db = ChatDatabase.getDatabase(application);
        mChatDao = db.chatDao();
        mAllChats = mChatDao.getAll();
    }

    LiveData<List<Chat>> getAllChats(){
        return mAllChats;
    }

    public void insert(Chat chat){
        new insertAsyncTask(mChatDao).execute(chat);
    }

    private static class insertAsyncTask extends AsyncTask<Chat, Void, Void>{
        private ChatDao mAsyncTaskDao;
        insertAsyncTask(ChatDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Chat... params){
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
