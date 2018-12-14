package com.johnwilliams.qq.Activities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.johnwilliams.qq.R;
import com.johnwilliams.qq.lib.Emoj.EmoViewPagerAdapter;
import com.johnwilliams.qq.lib.Emoj.EmoteAdapter;
import com.johnwilliams.qq.lib.EmoticonsEditText.EmoticonsEditText;
import com.johnwilliams.qq.lib.Emoj.FaceText;
import com.johnwilliams.qq.lib.Emoj.FaceTextUtils;
import com.johnwilliams.qq.lib.XListView.XListView;
import com.johnwilliams.qq.tools.Connection.MessageSender;
import com.johnwilliams.qq.tools.Constant;
import com.johnwilliams.qq.tools.Message.ChatMessage;
import com.johnwilliams.qq.tools.Message.MessageAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, XListView.IXListViewListener, EventListener {

    // Handler Messages
    public static final int NEW_MESSAGE = 0;
    public static ChatMessageHandler chatMessageHandler;

    // Basic info
    private String friend_name;
    private String friend_stunum;
    private String friend_ip;
    private String my_stunum;

    // Ui
    // Buttons
    private Button btn_chat_emoj;
    private Button btn_chat_send;
    private Button btn_chat_add;
    private Button btn_chat_keyboard;
    private Button btn_speak;
    private Button btn_chat_voice;

    // ChatMessage List
    private XListView mListView;

    // layout
    private LinearLayout layout_more;
    private LinearLayout layout_emoj;
    private LinearLayout layout_add;
    private RelativeLayout layout_record;


    private ViewPager pager_emoj;
    private TextView tv_picture;
    private TextView tv_camera;
    private TextView tv_location;
    private TextView tv_voice_tips;
    private ImageView iv_record;

    private Drawable[] drawable_Anims;

    // Input EditText
    EmoticonsEditText edit_user_comment;
    final public static MessageSender messageSender = new MessageSender();

    public static class ChatMessageHandler extends Handler{
        private WeakReference<ChatActivity> mActivity;

        public ChatMessageHandler(ChatActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            ChatActivity chatActivity = mActivity.get();
            if (msg.what == NEW_MESSAGE){
                chatActivity.initOrRefresh();
                Toast.makeText(chatActivity, R.string.new_message, Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initData();
        initView();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    void initView(){
        //Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView title = myToolbar.findViewById(R.id.toolbar_title);
        TextView online = myToolbar.findViewById(R.id.online);
        title.setText(friend_name.isEmpty() ? friend_stunum : friend_name);
        try{
            friend_ip = LoginActivity.connectionTool.getIp(friend_stunum);
            if (friend_name.equals("n")){
                online.setText(R.string.offline);
            } else {
                online.setText(R.string.online);
            }
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        mListView = (XListView)findViewById(R.id.mListView);
        initBottomView();
        initXListView();
    }

    private void initBottomView() {
        btn_chat_add = findViewById(R.id.btn_chat_add);
        btn_chat_emoj = findViewById(R.id.btn_chat_emo);
        btn_chat_keyboard = findViewById(R.id.btn_chat_keyboard);
        btn_chat_voice = findViewById(R.id.btn_chat_voice);
        btn_chat_send = findViewById(R.id.btn_chat_send);

        btn_chat_add.setOnClickListener(this);
        btn_chat_emoj.setOnClickListener(this);
        btn_chat_voice.setOnClickListener(this);
        btn_chat_keyboard.setOnClickListener(this);
        btn_chat_send.setOnClickListener(this);

        layout_more = findViewById(R.id.layout_more);
        layout_emoj = findViewById(R.id.layout_emo);
        layout_add = findViewById(R.id.layout_add);
        initAddView();
        initEmojView();

        btn_speak = findViewById(R.id.btn_speak);

        edit_user_comment = findViewById(R.id.edit_user_comment);
        edit_user_comment.setOnClickListener(this);
        edit_user_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)){// inputing
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {// after clear the text
                    if (btn_chat_voice.getVisibility() != View.VISIBLE){
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initAddView(){

    }

    List<FaceText> emojs;

    private void initEmojView(){
        pager_emoj = findViewById(R.id.pager_emo);
        emojs = FaceTextUtils.faceTexts;
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i){
            views.add(getGridView(i));
        }

        pager_emoj.setAdapter(new EmoViewPagerAdapter(views));
    }

    private View getGridView(final int i){
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridView = view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<>();
        if (i == 0){
            list.addAll(emojs.subList(0, 21));
        } else {
            list.addAll(emojs.subList(21, emojs.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this, list);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try{
                    if (edit_user_comment != null && !TextUtils.isEmpty(key)){
                        int start = edit_user_comment.getSelectionStart();
                        CharSequence content = edit_user_comment.getText().insert(start, key);
                        edit_user_comment.setText(content);

                        CharSequence info = edit_user_comment.getText();
                        if (info instanceof Spannable){
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText, start + key.length());
                        }
                    }
                } catch (Exception e){

                }
            }
        });
        return view;
    }

    MessageAdapter mAdapter;
    
    private void initXListView(){
        mListView.setPullLoadEnable(false);

        mListView.setPullRefreshEnable(true);

        mListView.setXListViewListener(this);
        mListView.setDividerHeight(0);

        initOrRefresh();
        mListView.setSelection(mAdapter.getCount() - 1);
        mListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                // Hide input method
                InputMethodManager inputMethodManager = (InputMethodManager)
                        (getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE));
                if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                layout_more.setVisibility(View.GONE);
                layout_add.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.VISIBLE);
                btn_chat_keyboard.setVisibility(View.GONE);
                btn_chat_send.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    public void onRefresh(){

    }

    @Override
    public void onLoadMore(){

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_chat_send:
                ChatMessage chatMessage = new ChatMessage(
                        my_stunum, friend_stunum,
                        edit_user_comment.getText().toString(),
                        new Date().getTime(), ChatMessage.MSG_TYPE.TEXT,
                        ChatMessage.MSG_STATUS.SENDING);
                boolean success = messageSender.SendMessage(chatMessage);

                if (success){
                    chatMessage.setStatus(ChatMessage.MSG_STATUS.SENT);
                    Toast.makeText(this, "发送成功", Toast.LENGTH_LONG).show();
                    edit_user_comment.setText("");
                } else {
                    chatMessage.setStatus(ChatMessage.MSG_STATUS.FAILED);
                    Toast.makeText(this, "发送失败", Toast.LENGTH_LONG).show();
                }
                mAdapter.add(chatMessage);
                mListView.setSelection(mAdapter.getCount() - 1);
                break;
        }
    }

    @Override
    public void onDestroy(){
        try {
            messageSender.ConnectionEnd();
        } catch (Exception e){

        }
        super.onDestroy();
    }

    void initData(){
        chatMessageHandler = new ChatMessageHandler(this);
        friend_name = getIntent().getExtras().getString(Constant.FRIEND_NAME_EXTRA);
        friend_stunum = getIntent().getExtras().getString(Constant.FRIEND_STUNUM_EXTRA);
        my_stunum = getIntent().getExtras().getString(Constant.MY_STUNUM_EXTRA);

        messageSender.DataInit(my_stunum, friend_stunum);
        try {
            messageSender.ConnectionInit();
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void initOrRefresh(){
        if (mAdapter == null){// init
            mAdapter = new MessageAdapter(this, initMsg());
            mListView.setAdapter(mAdapter);
        } else {

            MainActivity.messageReceiver.receiveMsg();
            int unread_number = MainActivity.messageReceiver.unread_num;
            if (unread_number != 0){
//                for (int i = 0; i < unread_number; i++){
//                    mAdapter.add(MainActivity.messageReceiver.get(i));
//                }
                mAdapter.addAll(MainActivity.messageReceiver.readMsg());
                mListView.setSelection(mAdapter.getCount() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    List<ChatMessage> initMsg(){
        List<ChatMessage> list = new ArrayList<>();
        list.add(new ChatMessage(my_stunum, friend_stunum, "佳佳我爱你", new Date().getTime(), ChatMessage.MSG_TYPE.TEXT, ChatMessage.MSG_STATUS.SENT));
        list.add(new ChatMessage(friend_name, my_stunum, "亮亮我爱你", new Date().getTime(), ChatMessage.MSG_TYPE.TEXT, ChatMessage.MSG_STATUS.SENDING));
        return list;
    }

}
