package com.johnwilliams.qq.tools.Contact;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Contact.class}, version = 1, exportSchema = false)
public abstract class ContactDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();

    private static volatile ContactDatabase INSTANCE;

    static ContactDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (ContactDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ContactDatabase.class, "contacts_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback sRoomDatabaseCallback =
            new Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>{

        private final ContactDao mDao;

        PopulateDbAsync(ContactDatabase db){
            mDao = db.contactDao();
        }

        @Override
        protected Void doInBackground(final Void... params){
//            mDao.deleteAll();
//            Contact contact = new Contact("2016011452", "赵文亮", false);
//            mDao.insert(contact);
//            contact = new Contact("2016110226", "白露佳", true);
//            mDao.insert(contact);
            return null;
        }
    }
}
