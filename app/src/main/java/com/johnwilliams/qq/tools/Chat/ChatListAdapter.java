package com.johnwilliams.qq.tools.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.johnwilliams.qq.Activities.ChatActivity;
import com.johnwilliams.qq.Activities.MainActivity;
import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Utils;
import com.johnwilliams.qq.tools.RecyclerItemClickListener;
import com.johnwilliams.qq.tools.RecyclerItemLongClickListener;
import com.johnwilliams.qq.tools.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>{

    class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final ImageView chat_profile;
        private final TextView chat_name;
        private final TextView chat_last_msg;
        private final TextView chat_time;
        private final TextView chat_unread;

        private RecyclerItemClickListener mClickListener;
        private RecyclerItemLongClickListener mLongClickListener;

        private ChatViewHolder(View itemView, RecyclerItemClickListener listener, RecyclerItemLongClickListener longClickListener){
            super(itemView);
            chat_profile = itemView.findViewById(R.id.chat_profile);
            chat_name = itemView.findViewById(R.id.chat_name);
            chat_last_msg = itemView.findViewById(R.id.last_msg);
            chat_time = itemView.findViewById(R.id.chat_time);
            chat_unread = itemView.findViewById(R.id.chat_unread);
            mClickListener = listener;
            mLongClickListener = longClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view){
            mClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view){
            mLongClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }

        private void ClearView(){
            chat_profile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.qq));
            chat_name.setText("");
            chat_last_msg.setText("");
            chat_time.setText("");
            chat_unread.setVisibility(TextView.INVISIBLE);
        }
    }

    private final LayoutInflater mInflater;
    private List<Chat> mChats;
    private List<Chat> mChatsCopy;
    private Context mContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private SimpleDateFormat sdf_today = new SimpleDateFormat("HH:mm", Locale.CHINA);

    private RecyclerItemClickListener mClickListener;
    private RecyclerItemLongClickListener mLongClickListener;

    public ChatListAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        mContext = context;
        initTimeZone();
        mClickListener = new RecyclerItemClickListener(){
            @Override
            public void onClick(View view, int position){
                startChat(position);
            }
        };
        mLongClickListener = new RecyclerItemLongClickListener(){
            @Override
            public void onLongClick(View view, int postion){
                removeAt(postion);
            }
        };
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = mInflater.inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(itemView, mClickListener, mLongClickListener);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position){
        if (mChats != null){
            Chat current = mChats.get(position);

            //profile
            holder.chat_profile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.github));

            //name
            if (!current.name.isEmpty()){
                holder.chat_name.setText(current.name);
            } else{
                holder.chat_name.setText(current.student_number);
            }

            // last message
            holder.chat_last_msg.setText(current.last_msg);

            //unread
            if (current.unread == 0) {
                holder.chat_unread.setVisibility(TextView.INVISIBLE);
            } else {
                holder.chat_unread.setVisibility(TextView.VISIBLE);
                if (current.unread > 99)
                    holder.chat_unread.setText(R.string.ninety_nine_plus);
                else
                    holder.chat_unread.setText(String.valueOf(current.unread));
            }

            //date
            holder.chat_time.setText(TimeUtil.formatTime(current.time));
        }
        else{
            holder.ClearView();
        }
    }

    public void setChats(List<Chat> chats){
        mChats = chats;
        mChatsCopy = new ArrayList<>(mChats);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return (mChats != null) ? mChats.size() : 0;
    }

    public void filter(String text){
        if (mChats == null){
            return;
        }
        mChats.clear();
        if (text.isEmpty()){
            mChats.addAll(mChatsCopy);
        } else {
            text = text.toLowerCase();
            for (Chat chat : mChatsCopy){
                if (chat.name.contains(text) || chat.last_msg.contains(text) || chat.student_number.contains(text)){
                    mChats.add(chat);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void initTimeZone(){
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        sdf_today.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    private void removeAt(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("是否刪除？").setTitle("刪除聊天记录");
        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.what = Utils.REMOVE_CHAT;
                msg.obj = position;
                MainActivity.mainMessageHandler.sendMessage(msg);
            }
        });
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void startChat(int position){
        Chat chat = mChats.get(position);
        String friend_stunum = chat.student_number;
        String friend_name = chat.name;
        try {
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra(Utils.FRIEND_STUNUM_EXTRA, friend_stunum);
            intent.putExtra(Utils.MY_STUNUM_EXTRA, MainActivity.my_stunum);
            intent.putExtra(Utils.FRIEND_NAME_EXTRA, friend_name);
            mContext.startActivity(intent);
        } catch (Exception e){
            Toast.makeText(mContext, R.string.connection_error, Toast.LENGTH_SHORT).show();
        }
    }
}
