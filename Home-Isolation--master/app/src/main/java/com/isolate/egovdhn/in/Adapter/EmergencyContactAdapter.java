package com.isolate.egovdhn.in.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.isolate.egovdhn.in.Models.NotifModel;
import com.isolate.egovdhn.in.Models.ResponseGetContacts;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ContactViewHolder> {
    ContactListener listener;
    ArrayList<ResponseGetContacts.EmergencyContact> contacts;

    public EmergencyContactAdapter(ContactListener listener, ArrayList<ResponseGetContacts.EmergencyContact> contacts) {
        this.listener = listener;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.emergency_contact_card, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.contact_number.setText(contacts.get(position).getNumber());
        holder.contact_desc.setText(contacts.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView contact_number;
        public TextView contact_desc;
        ContactListener listener;
        public ContactViewHolder(@NonNull View itemView, ContactListener listener) {
            super(itemView);
            this.listener = listener;
            contact_number = itemView.findViewById(R.id.phone_number);
            contact_desc = itemView.findViewById(R.id.phone_desc);
            contact_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactViewHolder.this.listener.onClick(getAdapterPosition());
                }
            });
        }
    }
    public interface ContactListener{
        void onClick(int itemPosition);
    }
}

