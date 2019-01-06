package com.johnwilliams.qq.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Contact.Contact;
import com.johnwilliams.qq.Adapters.ContactListAdapter;
import com.johnwilliams.qq.tools.Contact.ContactViewModel;

import java.util.List;

public class ContactFragment extends MyFragment{
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView contactList;
    public ContactViewModel mContactViewModel;
    public ContactListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView(){
        contactList = (RecyclerView)findViewById(R.id.list_contact);
        mAdapter = new ContactListAdapter(getContext());
        contactList.setAdapter(mAdapter);
        contactList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        mContactViewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable List<Contact> contacts) {
                mAdapter.setContacts(contacts);
            }
        });
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refresh();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
