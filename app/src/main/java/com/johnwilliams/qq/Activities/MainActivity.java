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
import com.johnwilliams.qq.fragments.SettingFragment;
import com.johnwilliams.qq.tools.Chat.Chat;
import com.johnwilliams.qq.tools.Connection.MessageReceiver;
import com.johnwilliams.qq.tools.Constant;
import com.johnwilliams.qq.tools.Contact.Contact;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;


public class MainActivity extends FragmentActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    // top bar
    private View top_bar;
    private SearchView searchView;
    private ImageButton add_button;
    //tabs
    private Button[] mTabs;
    private ChatFragment chatFragment;
    private ContactFragment contactFragment;
    private SettingFragment settingFragment;
    private Fragment[] mFragments;
    private int tab_index;
    private int cur_tab_index;
    public static String my_stunum;

    public static MessageReceiver messageReceiver = new MessageReceiver();
    public static MainMessageHandler mainMessageHandler;
    public static boolean contact_online;

    public static class MainMessageHandler extends Handler {
        private WeakReference<MainActivity> mActivity;

        public MainMessageHandler(MainActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            MainActivity mainActivity = mActivity.get();
            int position;
            switch (msg.what){
                case Constant.NEW_MESSAGE:
                    //TODO
                    break;
                case Constant.CLEAR_CHAT:
                    ((ChatFragment)mainActivity.mFragments[0]).mChatViewModel.clear();
                    break;
                case Constant.CLEAR_CONTACT:
                    ((ContactFragment)mainActivity.mFragments[1]).mContactViewModel.clear();
                    break;
                case Constant.REMOVE_CHAT:
                    position = (int)msg.obj;
                    ((ChatFragment)mainActivity.mFragments[0]).mChatViewModel.removeAt(position);
                    break;
                case Constant.REMOVE_CONTACT:
                    position = (int)msg.obj;
                    ((ContactFragment)mainActivity.mFragments[1]).mContactViewModel.removeAt(position);
                    break;
                case Constant.NEW_CHAT:
                    Chat chat = (Chat)msg.obj;
                    ((ChatFragment)mainActivity.mFragments[0]).mChatViewModel.insert(chat);
                    break;
                case Constant.UPDATE_CONTACT:
                    Contact contact = (Contact)msg.obj;
                    ((ContactFragment)mainActivity.mFragments[1]).mContactViewModel.update(contact);
                    break;
                default:
                    break;
            }
//            if (msg.what == Constant.NEW_MESSAGE){
//                // TODO: update database
//            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("Test", "There must be an output");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        my_stunum = getIntent().getExtras().getString(Constant.MY_STUNUM_EXTRA);
        mainMessageHandler = new MainMessageHandler(this);
        try {
            messageReceiver.run();
            Toast.makeText(this, R.string.server_on, Toast.LENGTH_SHORT);
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
        top_bar = findViewById(R.id.top_bar);
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
        settingFragment = new SettingFragment();
        mFragments = new Fragment[]{chatFragment, contactFragment, settingFragment};

        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, contactFragment).commit();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, chatFragment).commit();
    }

    public void onTabSelect(View view){
        switch (view.getId()){
            case R.id.tab_chat_btn:
                tab_index = 0;
                top_bar.setVisibility(View.VISIBLE);
                break;
            case R.id.tab_contact_btn:
                tab_index = 1;
                top_bar.setVisibility(View.VISIBLE);
                break;
            case R.id.tab_set_btn:
                tab_index = 2;
                top_bar.setVisibility(View.GONE);
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
        try {
            if (my_stunum != null){
                LoginActivity.connectionTool.Logout(my_stunum);
                LoginActivity.connectionTool.socket.close();
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
                if (student_number.length() != 10 || !student_number.matches(Constant.STU_NUM_REGEX)){
                    wrong_number = true;
                }

                if (student_number.equals(my_stunum)){
                    Toast.makeText(this, R.string.add_myself_error, Toast.LENGTH_SHORT).show();
                    break;
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
                } else if (reply.matches(Constant.IPV4_REGEX)){
                    wrong_number = false;
                } else if (reply.equals("Error")){
                    Toast.makeText(this, R.string.never_signup, Toast.LENGTH_SHORT).show();
                    break;
                }

                if (wrong_number){
                    Toast.makeText(this, R.string.wrong_stunum, Toast.LENGTH_SHORT).show();
                    break;
                } else if (((ContactFragment)mFragments[1]).mAdapter.getItemCount() != 0){
                    Toast.makeText(this, R.string.contact_exist, Toast.LENGTH_SHORT).show();
                    break;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("是否添加" + searchView.getQuery() + "?").setTitle("添加联系人");
                builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ContactFragment)mFragments[1]).mContactViewModel.insert(new Contact(student_number, "", MainActivity.contact_online));
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
