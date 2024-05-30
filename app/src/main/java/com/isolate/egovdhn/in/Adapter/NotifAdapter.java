package com.isolate.egovdhn.in.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.isolate.egovdhn.in.Models.NotifModel;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.NotifViewHolder> {
    NotifListener listener;
    ArrayList<NotifModel> notifications;

    public NotifAdapter(NotifListener listener, ArrayList<NotifModel> notifications) {
        this.listener = listener;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotifViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        holder.not_title.setText(notifications.get(position).getContent());
//        holder.not_mess.setText(notifications.get(position).not_mess);
        //String[] strArray = notifications.get(position).getTimestamp().split(" ");
        try {
            Date source = HelperClass.serverDateFormat().parse(notifications.get(position).getTimestamp());
            holder.not_date.setText(HelperClass.dateFormatter(source));
            holder.not_time.setText(HelperClass.timeFormatter(source));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotifViewHolder extends RecyclerView.ViewHolder{

        public TextView not_title;
        public TextView not_mess;
        public TextView not_date;
        public TextView not_time;
        public LinearLayout not_card;
        NotifListener listener;
        public NotifViewHolder(@NonNull View itemView, NotifListener listener) {
            super(itemView);
            this.listener = listener;
            not_title = itemView.findViewById(R.id.not_title);
            not_mess = itemView.findViewById(R.id.not_mess);
            not_date = itemView.findViewById(R.id.not_date);
            not_time = itemView.findViewById(R.id.not_time);
            not_card = itemView.findViewById(R.id.not_card);
            not_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotifViewHolder.this.listener.onClick(getAdapterPosition());
                }
            });
        }
    }
    public interface NotifListener{
        void onClick(int itemPosition);
    }
}

