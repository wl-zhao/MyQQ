package com.johnwilliams.qq.tools.Contact;

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

import com.johnwilliams.qq.Activities.ChatActivity;
import com.johnwilliams.qq.Activities.LoginActivity;
import com.johnwilliams.qq.Activities.MainActivity;
import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Chat.Chat;
import com.johnwilliams.qq.tools.Utils;
import com.johnwilliams.qq.tools.RecyclerItemClickListener;
import com.johnwilliams.qq.tools.RecyclerItemLongClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private final ImageView contact_profile;
        private final TextView contact_name;
        private final TextView online_state;

        private RecyclerItemClickListener mClickListener;
        private RecyclerItemLongClickListener mLongClickListener;

        private ContactViewHolder(View itemView, RecyclerItemClickListener listener, RecyclerItemLongClickListener longClickListener){
            super(itemView);
            contact_profile = itemView.findViewById(R.id.contact_profile);
            contact_name = itemView.findViewById(R.id.contact_name);
            online_state = itemView.findViewById(R.id.online_state);
            mClickListener = listener;
            mLongClickListener = longClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        private void ClearView(){
            contact_profile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.qq));
            contact_name.setText("");
            online_state.setText("");
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
    }

    private final LayoutInflater mInflater;
    private List<Contact> mContacts;
    private List<Contact> mContactsCopy;
    private Context mContext;

    private RecyclerItemClickListener mClickListener;
    private RecyclerItemLongClickListener mLongClickListener;

    private boolean contact_online_inited = false;

    public ContactListAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mClickListener = new RecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                startChat(position);
            }
        };
        mLongClickListener = new RecyclerItemLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {
                removeAt(position);
            }
        };
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = mInflater.inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(itemView, mClickListener, mLongClickListener);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position){
        if (mContacts != null){
            Contact current = mContacts.get(position);
            holder.contact_profile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.github));
            String online_state = current.online ?
                    mContext.getResources().getString(R.string.online) :
                    mContext.getResources().getString(R.string.offline);
            if (current.name.isEmpty()){
                holder.contact_name.setText(current.student_number);
                holder.online_state.setText(online_state);
            } else {
                holder.contact_name.setText(current.name);
                holder.online_state.setText(online_state + current.student_number);
            }
        }
        else{
            holder.ClearView();
        }
    }

    public void setContacts(List<Contact> contacts){
        mContacts = contacts;
        mContactsCopy = new ArrayList<>(mContacts);
        if (!contact_online_inited){
            contact_online_inited = true;
            refresh();
        }
        notifyDataSetChanged();
    }

    public void addContact(Contact contact){
        mContacts.add(contact);
        mContactsCopy.add(contact);
        notifyDataSetChanged();
    }

    private void removeAt(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("是否刪除？").setTitle("刪除联系人");
        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.what = Utils.REMOVE_CONTACT;
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

    @Override
    public int getItemCount(){
        return (mContacts != null) ? mContacts.size() : 0;
    }

    public void filter(String text){
        mContacts.clear();
        if (text.isEmpty()){
            mContacts.addAll(mContactsCopy);
        } else {
            text = text.toLowerCase();
            for (Contact contact : mContactsCopy){
                if (contact.name.contains(text) || contact.student_number.contains(text)){
                    mContacts.add(contact);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void startChat(int position){
        Contact contact = mContacts.get(position);
        String friend_stunum = contact.student_number;
        String friend_name = contact.name;

        // Add new chat
        Message msg = new Message();
        msg.what = Utils.NEW_CHAT;
        Chat chat = new Chat(friend_stunum, friend_name);
        chat.time = new Date().getTime();

        msg.obj = chat;
        MainActivity.mainMessageHandler.sendMessage(msg);

        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(Utils.FRIEND_STUNUM_EXTRA, friend_stunum);
        intent.putExtra(Utils.MY_STUNUM_EXTRA, MainActivity.my_stunum);
        intent.putExtra(Utils.FRIEND_NAME_EXTRA, friend_name);
        mContext.startActivity(intent);
    }

    public void refresh(){
        try {
            for (Contact contact : mContactsCopy){
                String reply = LoginActivity.connectionTool.getIp(contact.student_number);
                contact.online = reply.matches(Utils.IPV4_REGEX);
                Message msg = new Message();
                msg.what = Utils.UPDATE_CONTACT;
                msg.obj = contact;
                MainActivity.mainMessageHandler.sendMessage(msg);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
