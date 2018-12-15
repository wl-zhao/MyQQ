package com.johnwilliams.qq.tools.Contact;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private ContactRepository mRepository;
    private LiveData<List<Contact>> mAllContacts;

    public ContactViewModel(Application application){
        super(application);
        mRepository = new ContactRepository(application);
        mAllContacts = mRepository.getAllContacts();
    }

    public LiveData<List<Contact>> getAllContacts(){
        return mAllContacts;
    }

    public void insert(Contact chat){
        mRepository.insert(chat);
    }

    public void clear(){
        mRepository.clear();
    }
}
