package com.johnwilliams.qq.tools;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.johnwilliams.qq.R;
import com.johnwilliams.qq.tools.Contact.Contact;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupAdapter extends RecyclerView.Adapter<CreateGroupAdapter.CreateGroupViewHolder>{
    public static class CreateGroupViewHolder extends RecyclerView.ViewHolder {
        private final RadioButton rb_add_to_group;
        private final TextView contact_name;

        public CreateGroupViewHolder(View itemView) {
            super(itemView);
            rb_add_to_group = itemView.findViewById(R.id.rb_add_to_group);
            contact_name = itemView.findViewById(R.id.cg_contact_name);
        }

        private void ClearView(){
            contact_name.setText("");
        }
    }

    private List<Contact> mContacts;

    public CreateGroupAdapter() {
    }

    @NonNull
    @Override
    public CreateGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_group, parent, false);
        return new CreateGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CreateGroupViewHolder holder, int position){
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
        mContacts = contacts;
        notifyDataSetChanged();
    }
}
