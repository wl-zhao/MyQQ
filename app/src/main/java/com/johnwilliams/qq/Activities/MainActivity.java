package com.johnwilliams.qq.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.johnwilliams.qq.R;
import com.johnwilliams.qq.fragments.ChatFragment;
import com.johnwilliams.qq.fragments.ContactFragment;
import com.johnwilliams.qq.tools.Chat.ChatViewModel;
import com.johnwilliams.qq.tools.Connection.ConnectionTool;
import com.johnwilliams.qq.tools.Connection.MessageReceiver;
import com.johnwilliams.qq.tools.Constant;
import com.johnwilliams.qq.tools.Contact.Contact;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.regex.Pattern;


public class MainActivity extends FragmentActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    private SearchView searchView;
    private ImageButton add_button;
    //tabs
    private Button[] mTabs;
    private ChatFragment chatFragment;
    private ContactFragment contactFragment;
    private Fragment settingFragment;
    private Fragment[] mFragments;
    private int tab_index;
    private int cur_tab_index;
    public static String my_stunum;

    public static MessageReceiver messageReceiver = new MessageReceiver();
    public static ChatListMessageHandler chatListMessageHandler;
    public static boolean contact_online;

    public static class ChatListMessageHandler extends Handler {
        private WeakReference<MainActivity> mActivity;

        public ChatListMessageHandler(MainActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            MainActivity mainActivity = mActivity.get();
            if (msg.what == ChatActivity.NEW_MESSAGE){
                // TODO: update database
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("Test", "There must be an output");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        my_stunum = getIntent().getExtras().getString(Constant.MY_STUNUM_EXTRA);
        chatListMessageHandler = new ChatListMessageHandler(this);
        try {
            messageReceiver.run();
            Toast.makeText(this, R.string.server_on, Toast.LENGTH_LONG);
        } catch (Exception e){
            Log.d("ConnectionError", e.getMessage());
        }
    }

    protected void removeUnderline(){
        if (searchView != null){
            try{
                Class<?> argClass = searchView.getClass();
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                ownField.setAccessible(true);
                View mView = (View)ownField.get(searchView);
                mView.setBackgroundColor(Color.TRANSPARENT);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    protected void initView(){
        add_button = findViewById(R.id.addFriendButton);
        add_button.setOnClickListener(this);
        searchView = findViewById(R.id.search_friend);
        searchView.setFocusable(false);
        searchView.setOnQueryTextListener(this);
        mTabs = new Button[3];
        mTabs[0] = (Button)findViewById(R.id.tab_chat_btn);
        mTabs[1] = (Button)findViewById(R.id.tab_contact_btn);
        mTabs[2] = (Button)findViewById(R.id.tab_set_btn);
        mTabs[0].setSelected(true);
        initTabs();
        removeUnderline();
    }

    protected void initTabs(){
        chatFragment = new ChatFragment();
        contactFragment = new ContactFragment();
        settingFragment = new Fragment();
        mFragments = new Fragment[]{chatFragment, contactFragment, settingFragment};

        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, chatFragment).commit();
    }

    public void onTabSelect(View view){
        switch (view.getId()){
            case R.id.tab_chat_btn:
                tab_index = 0;
                break;
            case R.id.tab_contact_btn:
                tab_index = 1;
                break;
            case R.id.tab_set_btn:
                tab_index = 2;
                break;
        }
        if (cur_tab_index != tab_index){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mFragments[tab_index]).commit();
        }
        mTabs[cur_tab_index].setSelected(false);
        mTabs[tab_index].setSelected(true);
        cur_tab_index = tab_index;
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(this, R.string.test_receive, Toast.LENGTH_LONG).show();
        try {
            if (my_stunum != null){
                LoginActivity.connectionTool.Logout(my_stunum);
                LoginActivity.connectionTool.socket = null;
            }
        } catch (Exception e){

        }
        finish();
    }

    @Override
    public void onDestroy(){
        messageReceiver.stop();
        super.onDestroy();
    }

    private boolean filter(String query){
        switch (tab_index){
            case 0:
                ((ChatFragment)mFragments[0]).mAdapter.filter(query);
                break;
            case 1:
                ((ContactFragment)mFragments[1]).mAdapter.filter(query);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        return filter(query);
    }

    @Override
    public boolean onQueryTextChange(String newText){
        return filter(newText);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.addFriendButton:
                contact_online = true;
                final String student_number = searchView.getQuery().toString();
                boolean wrong_number = false;
                if (student_number.length() != 10 || !student_number.matches("^[0-9]*$")){
                    wrong_number = true;
                }
                String reply = "";
                if (!wrong_number){
                    try{
                        reply = LoginActivity.connectionTool.getIp(student_number);
                    } catch (Exception e){

                    }
                }
                if (reply.equals("n")){
                    contact_online = false;
                } else if (reply.equals("Incorrect No.")){
                    wrong_number = true;
                } else if (reply.matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}↵\n" +
                        "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")){
                    wrong_number = false;
                }
                if (wrong_number){
                    Toast.makeText(this, R.string.wrong_stunum, Toast.LENGTH_LONG).show();
                    break;
                } else if (((ContactFragment)mFragments[1]).mAdapter.getItemCount() != 0){
                    Toast.makeText(this, R.string.contact_exist, Toast.LENGTH_LONG).show();
                    break;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("是否添加" + searchView.getQuery() + "?").setTitle("添加联系人");
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ContactFragment)mFragments[1]).mAdapter.addContact(new Contact(student_number, "", MainActivity.contact_online));
                    }
                });
                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                break;
        }
    }
}
