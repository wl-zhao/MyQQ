package com.johnwilliams.qq.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Chat.Chat;
import com.johnwilliams.qq.tools.Chat.ChatListAdapter;
import com.johnwilliams.qq.tools.Chat.ChatViewModel;

import java.util.List;

public class ChatFragment extends MyFragment{
    RecyclerView chatList;
    public ChatViewModel mChatViewModel;
    public ChatListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView(){
        chatList = (RecyclerView)findViewById(R.id.list_chat);

        mAdapter = new ChatListAdapter(getContext());
        chatList.setAdapter(mAdapter);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));
        mChatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        mChatViewModel.getAllChats().observe(this, new Observer<List<Chat>>() {
            @Override
            public void onChanged(@Nullable List<Chat> chats) {
                mAdapter.setChats(chats);
            }
        });
        //TODO: write class for chat history
    }
}
