package com.johnwilliams.qq.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Connection.MessageSender;
import com.johnwilliams.qq.tools.Contact.Contact;
import com.johnwilliams.qq.tools.Contact.ContactViewModel;
import com.johnwilliams.qq.tools.CreateGroupAdapter;
import com.johnwilliams.qq.tools.Message.ChatMessage;
import com.johnwilliams.qq.tools.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    CreateGroupAdapter mAdapter;
    RecyclerView mRecyclerView;
    ContactViewModel mContactViewModel;
    public static Button btn_create_group;
    String my_stunum;
    String friend_stunum;
    List<MessageSender> messageSenders = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initData();
        initView();
    }

    public void initView() {
        btn_create_group = findViewById(R.id.btn_create_now);
        btn_create_group.setAlpha(0.5f);
        btn_create_group.setClickable(false);
        mAdapter = new CreateGroupAdapter(my_stunum, friend_stunum);
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

    public void initData() {
        friend_stunum = getIntent().getExtras().getString(Utils.FRIEND_STUNUM_EXTRA);
        my_stunum = getIntent().getExtras().getString(Utils.MY_STUNUM_EXTRA);
    }

    public void submitGroup(View view) {
        final String members = mAdapter.getGroupMember();
        List<String> member_list = (List<String>)Utils.removeMyStunum(members, my_stunum, true);
        for (String member : member_list) {
            try {
                MessageSender messageSender = new MessageSender();
                messageSender.DataInit(my_stunum, member);
                messageSender.ConnectionInit();
                messageSenders.add(messageSender);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialog_view = inflater.inflate(R.layout.dialog_create_group, null);
        builder.setView(dialog_view).setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText group_name = dialog_view.findViewById(R.id.et_group_name);
                if (group_name.getText().toString().isEmpty()) {
                    showToast(R.string.group_name_cant_empty);
                } else {
                    Contact contact = new Contact(members, group_name.getText().toString(), false);
                    mContactViewModel.insert(contact);
                    showToast(R.string.create_group_successfully);
                    notifyOtherMembers(group_name.getText().toString(), members);
                    finish();
                }
            }
        })
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setTitle(R.string.create_group);
        builder.create().show();
    }

    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public void notifyOtherMembers(String group_name, String members) {
        members = members.replace(",", ";");
        for (MessageSender messageSender : messageSenders) {
            ChatMessage chatMessage = new ChatMessage(my_stunum, messageSender.friend_stunum, group_name + "$" + members,
                    new Date().getTime(), ChatMessage.MSG_TYPE.CMD, ChatMessage.MSG_STATUS.SENDING);
            boolean success = messageSender.SendMessage(chatMessage);
        }
    }
}
