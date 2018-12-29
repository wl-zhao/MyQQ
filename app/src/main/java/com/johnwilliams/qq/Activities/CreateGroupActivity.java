package com.johnwilliams.qq.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Contact.Contact;
import com.johnwilliams.qq.tools.Contact.ContactListAdapter;
import com.johnwilliams.qq.tools.Contact.ContactRepository;
import com.johnwilliams.qq.tools.Contact.ContactViewModel;
import com.johnwilliams.qq.tools.CreateGroupAdapter;

import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    CreateGroupAdapter mAdapter;
    RecyclerView mRecyclerView;
    ContactViewModel mContactViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initView();
    }

    public void initView() {
        mAdapter = new CreateGroupAdapter();
        mContactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        mContactViewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable List<Contact> contacts) {
                mAdapter.setContacts(contacts);
            }
        });
        mRecyclerView = findViewById(R.id.list_create_group);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }
}
