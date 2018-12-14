package com.johnwilliams.qq.tools.Contact;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.johnwilliams.qq.R;

import java.util.ArrayList;
import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    class ContactViewHolder extends RecyclerView.ViewHolder{
        private final ImageView contact_profile;
        private final TextView contact_name;
        private final TextView online_state;

        private ContactViewHolder(View itemView){
            super(itemView);
            contact_profile = itemView.findViewById(R.id.contact_profile);
            contact_name = itemView.findViewById(R.id.contact_name);
            online_state = itemView.findViewById(R.id.online_state);
        }

        private void ClearView(){
            contact_profile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.qq));
            contact_name.setText("");
            online_state.setText("");
        }
    }

    private final LayoutInflater mInflater;
    private List<Contact> mContacts;
    private List<Contact> mContactsCopy;
    private Context mContext;

    public ContactListAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = mInflater.inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(itemView);
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
        notifyDataSetChanged();
    }

    public void addContact(Contact contact){
        mContacts.add(contact);
        mContactsCopy.add(contact);
        notifyDataSetChanged();
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
}
