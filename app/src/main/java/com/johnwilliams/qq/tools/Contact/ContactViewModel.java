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

//    public void refresh(){
//        try {
//            for (Contact contact : mAllContacts.getValue()){
//                String reply = LoginActivity.connectionTool.getIp(contact.student_number);
//                contact.online = reply.matches(Utils.IPV4_REGEX);
//                mRepository.update(contact);
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public void update(Contact contact){
        mRepository.update(contact);
    }

    public void insert(Contact contact){
        mRepository.insert(contact);
    }

    public void clear(){
        mRepository.clear();
    }

    public void removeAt(int position){
        mRepository.removeAt(position);
    }

    public boolean contains(String query) {
        for (Contact contact : mAllContacts.getValue()) {
            if (contact.student_number.contains(query) || contact.name.contains(query)) {
                return true;
            }
        }
        return false;
    }
}
