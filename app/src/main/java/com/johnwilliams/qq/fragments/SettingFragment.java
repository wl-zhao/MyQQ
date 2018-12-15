package com.johnwilliams.qq.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.johnwilliams.qq.Activities.MainActivity;
import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Constant;
import com.johnwilliams.qq.tools.Contact.Contact;

public class SettingFragment extends MyFragment implements View.OnClickListener {

    private Button btn_clear_contacts;
    private Button btn_clear_chats;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView(){
        btn_clear_chats = (Button) findViewById(R.id.btn_clear_chats);
        btn_clear_contacts = (Button) findViewById(R.id.btn_clear_contacts);
        btn_clear_chats.setOnClickListener(this);
        btn_clear_contacts.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int message = Constant.DO_NOTHING;
        switch (v.getId()){
            case R.id.btn_clear_chats:
                message = Constant.CLEAR_CHAT;
                MainActivity.mainMessageHandler.sendEmptyMessage(Constant.CLEAR_CHAT);
                break;
            case R.id.btn_clear_contacts:
                message = Constant.CLEAR_CONTACT;
                MainActivity.mainMessageHandler.sendEmptyMessage(Constant.CLEAR_CONTACT);
                break;
        }

        if (message == Constant.DO_NOTHING){
            return;
        }

        final int sendMessage = message;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("是否清空?").setTitle("清空" + (sendMessage == Constant.CLEAR_CHAT ? "聊天记录" : "联系人"));
        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.mainMessageHandler.sendEmptyMessage(sendMessage);
            }
        });
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}
