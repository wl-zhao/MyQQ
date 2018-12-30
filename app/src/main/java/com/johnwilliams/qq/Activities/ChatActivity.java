package com.johnwilliams.qq.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.johnwilliams.qq.tools.Connection.MessageReceiver;
import com.johnwilliams.qq.tools.Connection.MessageSender;
import com.johnwilliams.qq.tools.RecordManager;
import com.johnwilliams.qq.tools.Utils;
import com.johnwilliams.qq.tools.Message.ChatMessage;
import com.johnwilliams.qq.tools.Message.MessageAdapter;
import com.johnwilliams.qq.tools.PermissionManager;
import com.johnwilliams.qq.tools.URIConverter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Date;

import cn.bmob.v3.BmobObject;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, XListView.IXListViewListener, EventListener {

    // Handler Messages
    public static ChatMessageHandler chatMessageHandler;

    // Basic info
    private String friend_name;
    private String friend_stunum;
    private List<String> friend_stunums;
    private String friend_ip;
    public String my_stunum;

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
    private TextView tv_file;
    private TextView tv_voice_tips;
    private ImageView iv_record;

    private Drawable[] drawable_Anims;

    // read file
    private static final int READ_REQUEST_CODE = 42;

    // record
    private RecordManager recordManager = new RecordManager(this);

    // Input EditText
    EmoticonsEditText edit_user_comment;
    public List<MessageSender> messageSenders = new ArrayList<>();
    public MessageSender fileSender;
    public MessageSender audioSender;

    public static class ChatMessageHandler extends Handler{
        private WeakReference<ChatActivity> mActivity;

        public ChatMessageHandler(ChatActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            ChatActivity chatActivity = mActivity.get();
            ChatMessage chatMessage;
            switch (msg.what){
                case Utils.NEW_MESSAGE:
                    chatMessage = (ChatMessage)msg.obj;
                    if (chatMessage.getType() == ChatMessage.MSG_TYPE.CMD) { // ignore command message
                        break;
                    }
                    if (chatMessage.getTo_stunum().contains(chatActivity.my_stunum)) {
                        chatActivity.mAdapter.add((ChatMessage)msg.obj);
                        Toast.makeText(chatActivity, R.string.new_message, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Utils.LOAD_DONE:
                    chatActivity.mAdapter.addAll((List<ChatMessage>)msg.obj);
                    break;
                case Utils.UPDATE_PROGRESS:
                    chatMessage = (ChatMessage) msg.obj;
                    chatActivity.mAdapter.updateMessage(chatMessage);
                    break;
                case Utils.UPDATE_RECORDING:
                    int value = (int)msg.obj / 20;
                    value = (value > 5) ? 5 : value;
                    chatActivity.setRecordVolume(value);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initData();
        initView();
        PermissionManager.CheckReadPermission(this);
        PermissionManager.CheckAudioPermission(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_group:
                Intent intent = new Intent(this, CreateGroupActivity.class);
                intent.putExtra(Utils.FRIEND_STUNUM_EXTRA, friend_stunum);
                intent.putExtra(Utils.MY_STUNUM_EXTRA, my_stunum);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            friend_ip = LoginActivity.connectionTool.getIp((String)Utils.removeMyStunum(friend_stunum, my_stunum, false));
            if (!Utils.isGroupChat(friend_stunum)) {
                if (friend_ip.equals("n")){
                    online.setText(R.string.offline);
                } else {
                    online.setText(R.string.online);
                }
            } else {
                online.setText("");
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
        initVoiceView();
    }

    private void initAddView(){
        tv_picture = findViewById(R.id.tv_picture);
        tv_camera = findViewById(R.id.tv_camera);
        tv_file = findViewById(R.id.tv_file);
        tv_picture.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        tv_file.setOnClickListener(this);
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

    // Voice
    private void initVoiceView() {
        layout_record = findViewById(R.id.layout_record);
        tv_voice_tips = findViewById(R.id.tv_voice_tips);
        iv_record = findViewById(R.id.iv_record);
        btn_speak.setOnTouchListener(new VoiceTouchListener(this));
        drawable_Anims = new Drawable[] {
                getDrawable(R.drawable.chat_icon_voice1),
                getDrawable(R.drawable.chat_icon_voice2),
                getDrawable(R.drawable.chat_icon_voice3),
                getDrawable(R.drawable.chat_icon_voice4),
                getDrawable(R.drawable.chat_icon_voice5),
                getDrawable(R.drawable.chat_icon_voice6),
        };
    }

    private void setRecordVolume(int value) {
        iv_record.setImageDrawable(drawable_Anims[value]);
    }
    private class VoiceTouchListener implements View.OnTouchListener {
        Context mContext;
        public VoiceTouchListener(Context context) {
            mContext = context;
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!Utils.checkSdCard()) {
                        Toast.makeText(mContext, getString(R.string.no_sdcard), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        recordManager.startRecording();
                        audioSender = new MessageSender();
                        audioSender.DataInit(my_stunum, friend_stunum);
                        audioSender.ConnectionInit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getY() < 0) {
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) { //cancel
                            recordManager.stopRecording(false);
                        } else {
                            Integer seconds = recordManager.stopRecording(true);
                            if (seconds > 1) {
                                ChatMessage chatMessage = new ChatMessage(my_stunum, friend_stunum, recordManager.getFileName(),
                                        new Date().getTime(), ChatMessage.MSG_TYPE.AUDIO, ChatMessage.MSG_STATUS.SENDING);
                                chatMessage.addFileLength();
                                chatMessage.setContent(chatMessage.getContent() + "?" + seconds.toString());
                                audioSender.SendMessage(chatMessage);
                                audioSender.SendFile(recordManager.getFileName(), chatMessage);
//                                mAdapter.add(chatMessage);
                            } else {
                                layout_record.setVisibility(View.GONE);
                                Toast.makeText(mContext, getString(R.string.too_short), Toast.LENGTH_SHORT).show();
                                recordManager.deleteFile();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                default:
                    return false;
            }
        }
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
        initOrRefresh();
        mListView.setSelection(mAdapter.getCount() - 1);
    }

    @Override
    public void onRefresh(){

    }

    @Override
    public void onLoadMore(){
        initOrRefresh();
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
                boolean success = false;
                for (MessageSender messageSender : messageSenders) {
                    if (messageSender.SendMessage(chatMessage)) {
                        success = true;
                    }
                }

                if (success){
                    chatMessage.setStatus(ChatMessage.MSG_STATUS.SENT);
                    Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
                    edit_user_comment.setText("");
                } else {
                    chatMessage.setStatus(ChatMessage.MSG_STATUS.FAILED);
                    Toast.makeText(this, "发送失败", Toast.LENGTH_SHORT).show();
                }
                mAdapter.add(chatMessage);
                mListView.setSelection(mAdapter.getCount() - 1);
                break;
            case R.id.btn_chat_add:
                if (layout_more.getVisibility() == View.GONE){
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    layout_emoj.setVisibility(View.GONE);

                } else {
                    if (layout_emoj.getVisibility() == View.VISIBLE){
                        layout_emoj.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tv_file:
                Toast.makeText(this, "load file", Toast.LENGTH_LONG).show();
                performFileSearch();
                layout_add.setVisibility(View.GONE);
                break;
            case R.id.btn_chat_voice:
                edit_user_comment.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;
            case R.id.btn_chat_keyboard:
                showEditState(false);
                break;
        }
    }

    @Override
    public void onDestroy(){
        try {
            for (MessageSender messageSender : messageSenders)
                messageSender.ConnectionEnd();
        } catch (Exception e){

        }
        super.onDestroy();
    }

    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    void initData(){
        chatMessageHandler = new ChatMessageHandler(this);
        friend_name = getIntent().getExtras().getString(Utils.FRIEND_NAME_EXTRA);
        friend_stunum = getIntent().getExtras().getString(Utils.FRIEND_STUNUM_EXTRA);
        my_stunum = getIntent().getExtras().getString(Utils.MY_STUNUM_EXTRA);
        friend_stunums = (List<String>) Utils.removeMyStunum(friend_stunum, my_stunum, true);
        for (String str : friend_stunums) {
            MessageSender messageSender = new MessageSender();
            messageSender.DataInit(my_stunum, str);
            messageSenders.add(messageSender);
            try {
                messageSender.ConnectionInit();
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
                mAdapter.addAll(MainActivity.messageReceiver.readMsg());
                mListView.setSelection(mAdapter.getCount() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showEditState(boolean isEmo) {
        edit_user_comment.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_user_comment.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emoj.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView();
        }
    }
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_user_comment, 0);
        }
    }


    List<ChatMessage> initMsg(){
        MessageReceiver.fetchMessages(my_stunum, friend_stunum);
        return new ArrayList<>();
    }

    public void performFileSearch(){
        try {
            fileSender = new MessageSender();
            fileSender.DataInit(my_stunum, friend_stunum);
            fileSender.ConnectionInit();
        } catch (Exception e){

        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData){
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if (resultData != null){
                uri = resultData.getData();
                String localPath = URIConverter.getPathFromUri(this, uri);
                Toast.makeText(this, localPath, Toast.LENGTH_LONG).show();
                // TODO: send files
                ChatMessage chatMessage = new ChatMessage(my_stunum, friend_stunum, localPath, new Date().getTime(),
                        ChatMessage.MSG_TYPE.FILE, ChatMessage.MSG_STATUS.SENDING);
                chatMessage.addFileLength();
                try{
                    fileSender.SendMessage(chatMessage);
                    fileSender.SendFile(localPath, chatMessage);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private String getLocalPath(Uri uri){
        String localPath = null;
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            localPath = cursor.getString(columnIndex);
            cursor.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return localPath;
    }
}
