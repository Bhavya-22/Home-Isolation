package com.isolate.egovdhn.in.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isolate.egovdhn.in.Models.TelemedicineScheduleModel;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class TelemedicineScheduleAdapter extends RecyclerView.Adapter<TelemedicineScheduleAdapter.TelemedicineScheduleViewHolder> {

    TelemedicineScheduleListener listener;
    ArrayList<TelemedicineScheduleModel> schedules;

    public TelemedicineScheduleAdapter(TelemedicineScheduleListener listener, ArrayList<TelemedicineScheduleModel> schedules) {
        this.listener = listener;
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public TelemedicineScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TelemedicineScheduleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tele_card, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TelemedicineScheduleViewHolder holder, int position) {
        holder.doctor.setText("Doctor Name : " + schedules.get(position).doctor);

        try {
            Date source = HelperClass.serverDateFormat().parse(schedules.get(position).datetime);
            holder.date.setText(HelperClass.dateFormatter(source));
            holder.time.setText(HelperClass.timeFormatter(source));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public class TelemedicineScheduleViewHolder extends RecyclerView.ViewHolder{

        TextView date;
        TextView time;
        TextView link;
        TextView doctor;
        TelemedicineScheduleListener listener;

        public TelemedicineScheduleViewHolder(@NonNull View itemView, TelemedicineScheduleListener listener) {
            super(itemView);
            this.listener = listener;

            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            link = itemView.findViewById(R.id.meet_url);
            doctor = itemView.findViewById(R.id.doct_name);

            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TelemedicineScheduleViewHolder.this.listener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public interface TelemedicineScheduleListener{
        void onClick(int itemPosition);
    }
}
