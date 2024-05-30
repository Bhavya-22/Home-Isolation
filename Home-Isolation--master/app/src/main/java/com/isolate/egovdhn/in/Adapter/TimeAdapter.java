package com.isolate.egovdhn.in.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.isolate.egovdhn.in.Models.HealthRecordModel;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {
    //Initiliase array list
    ArrayList<HealthRecordModel> arrayListMember;

    //Create constructor


    public TimeAdapter(ArrayList<HealthRecordModel> arrayListMember) {
        this.arrayListMember = arrayListMember;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialise view
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.time_card_view, parent, false);
        return new TimeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeAdapter.ViewHolder holder, int position) {
        //Set member name on Textview
        Date date = null;
        try {
            date = HelperClass.serverDateFormat().parse(arrayListMember.get(position).timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        CardView cardView = holder.cardView;
        TextView pulse_value = cardView.findViewById(R.id.pulse_txt_value) ;
        TextView temp_value = cardView.findViewById(R.id.temp_txt_value) ;
        TextView bplow_value = cardView.findViewById(R.id.bplow_txt_value);
        TextView bphigh_value = cardView.findViewById(R.id.bphigh_txt_value);
        TextView spO2_value = cardView.findViewById(R.id.spO2_txt_value);
        TextView resr_value = cardView.findViewById(R.id.resr_txt_value);
        TextView tot = cardView.findViewById(R.id.tot);

        if(date!=null)
            tot.setText("Time: " + HelperClass.timeFormatter(date));
        else
            tot.setText("Not Available");
        pulse_value.setText(String.valueOf(arrayListMember.get(position).pulse));
        temp_value.setText(String.valueOf(arrayListMember.get(position).temperature));
        bplow_value.setText(String.valueOf(arrayListMember.get(position).bp_low));
        bphigh_value.setText(String.valueOf(arrayListMember.get(position).bp_high));
        spO2_value.setText(String.valueOf(arrayListMember.get(position).spo2));
        resr_value.setText(String.valueOf(arrayListMember.get(position).respiratory_rate));
    }

    @Override
    public int getItemCount() {
        return arrayListMember.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Initialise card view
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign variable
            cardView=itemView.findViewById(R.id.cardView_health);
//            cardView.setBackgroundResource(R.drawable.report_bg);
        }
    }
}
