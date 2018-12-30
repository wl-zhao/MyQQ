package com.johnwilliams.qq.tools;

import android.arch.persistence.room.Ignore;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.johnwilliams.qq.Activities.CreateGroupActivity;
import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Contact.Contact;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupAdapter extends RecyclerView.Adapter<CreateGroupAdapter.CreateGroupViewHolder>{
    public static class CreateGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final RadioButton rb_add_to_group;
        private final TextView contact_name;
        private RecyclerItemClickListener mClickListener;
        private boolean isChecked = false;

        public CreateGroupViewHolder(View itemView, RecyclerItemClickListener listener) {
            super(itemView);
            rb_add_to_group = itemView.findViewById(R.id.rb_add_to_group);
            contact_name = itemView.findViewById(R.id.cg_contact_name);
            mClickListener = listener;
            itemView.setOnClickListener(this);
            rb_add_to_group.setOnClickListener(this);
        }

        private void ClearView(){
            contact_name.setText("");
        }

        @Override
        public void onClick(View view) {
            mClickListener.onClick(view, getAdapterPosition());
            isChecked = !isChecked;
            rb_add_to_group.setChecked(isChecked);
        }
    }

    private List<Contact> mContacts;
    private List<Boolean> mChecked;
    private int checked_num = 0;
    private String my_stunum;
    private String friend_stunum;
    private RecyclerItemClickListener mClickListener;

    public CreateGroupAdapter(String my_stunum, String friend_stunum) {
        this.my_stunum = my_stunum;
        this.friend_stunum = friend_stunum;
        mClickListener = new RecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                mChecked.set(position, !mChecked.get(position));
                checked_num += mChecked.get(position) ? 1 : -1;
                CreateGroupActivity.btn_create_group.setClickable(checked_num != 0);
                CreateGroupActivity.btn_create_group.setAlpha(checked_num == 0 ? 0.5f : 1f);
            }
        };
    }


    @NonNull
    @Override
    public CreateGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_group, parent, false);
        return new CreateGroupViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(final CreateGroupViewHolder holder, final int position){
        if (mContacts != null) {
            Contact current = mContacts.get(position);
            if (current.name.isEmpty()){
                holder.contact_name.setText(current.student_number);
            } else {
                holder.contact_name.setText(current.name);
            }
        } else {
            holder.ClearView();
        }
    }

    @Override
    public int getItemCount(){
        return (mContacts != null) ? mContacts.size() : 0;
    }

    public void setContacts(List<Contact> contacts){
        mContacts = new ArrayList<>();
        mChecked = new ArrayList<>();
        for (Contact contact : contacts) {
            if (!contact.student_number.equals(friend_stunum) && !contact.student_number.contains(",")) {
                mContacts.add(contact);
                mChecked.add(false);
            }
        }
        notifyDataSetChanged();
    }

    // return format: owner_stunum?member1_stunum?member2_stunum?...?membern_stunum
    public String getGroupMember() {
        StringBuilder builder = new StringBuilder();
        builder.append(my_stunum);
        builder.append(",");
        builder.append(friend_stunum);
        for (int i = 0; i < mContacts.size(); ++i) {
            if (mChecked.get(i)) {
                builder.append(",");
                builder.append(mContacts.get(i).student_number);
            }
        }
        return builder.toString();
    }
}
