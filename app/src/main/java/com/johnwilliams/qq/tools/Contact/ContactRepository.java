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
        mContactDao = db.contactDao();
        mAllContacts = mContactDao.getAll();
    }

    LiveData<List<Contact>> getAllContacts(){
        return mAllContacts;
    }

    public void removeAt(int i){
        new removeAsyncTask(mContactDao).execute(mAllContacts.getValue().get(i));
    }

    public void update(Contact contact){
        new updateAsyncTask(mContactDao).execute(contact);
    }

    public void insert(Contact contact){
        new insertAsyncTask(mContactDao).execute(contact);
    }

    public void clear(){
        new clearAsyncTask(mContactDao).execute();
    }

    private static class removeAsyncTask extends AsyncTask<Contact, Void, Void>{
        private ContactDao mAsyncTaskDao;
        removeAsyncTask(ContactDao dao){
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Contact... params){
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Contact, Void, Void>{
        private ContactDao mAsyncTaskDao;
        updateAsyncTask(ContactDao dao){
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Contact... params){
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class clearAsyncTask extends AsyncTask<Void, Void, Void>{
        private ContactDao mAsyncTaskDao;
        clearAsyncTask(ContactDao dao){
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Void... params){
            mAsyncTaskDao.deleteAll();
            return null;
        }
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
