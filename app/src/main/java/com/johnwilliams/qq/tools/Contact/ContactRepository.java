package com.johnwilliams.qq.tools.Contact;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ContactRepository {
    private ContactDao mContactDao;
    private LiveData<List<Contact>> mAllContacts;

    ContactRepository(Application application){
        ContactDatabase db = ContactDatabase.getDatabase(application);
        mContactDao = db.chatDao();
        mAllContacts = mContactDao.getAll();
    }

    LiveData<List<Contact>> getAllContacts(){
        return mAllContacts;
    }

    public void insert(Contact contact){
        new insertAsyncTask(mContactDao).execute(contact);
    }

    private static class insertAsyncTask extends AsyncTask<Contact, Void, Void>{
        private ContactDao mAsyncTaskDao;
        insertAsyncTask(ContactDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Contact... params){
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
