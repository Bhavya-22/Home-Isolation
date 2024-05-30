package com.isolate.egovdhn.in.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isolate.egovdhn.in.Models.RecordsDateModel;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.util.ArrayList;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    //Initialise activity and array list
    private final Activity activity;
    ArrayList<RecordsDateModel> singleDateList;

    public DateAdapter(Activity activity, ArrayList<RecordsDateModel> singleDateList) {
        this.activity = activity;
        this.singleDateList = singleDateList;
    }

    @NonNull
    @Override
    public DateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.date_card_view,parent,false);
        return new DateAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DateAdapter.ViewHolder holder, int position) {
        //set group name to text view
        holder.textName.setText(HelperClass.dateFormatter(singleDateList.get(position).date));

        TimeAdapter timeAdapter = new TimeAdapter(singleDateList.get(position).singleDateRecords);

        //Initialse layout manager
        LinearLayoutManager linearLayoutMember = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
        //set layout manager
        holder.rv_member.setLayoutManager(linearLayoutMember);
        //set adapter
        holder.rv_member.setAdapter(timeAdapter);
        holder.textView2.setText(singleDateList.get(position).singleDateRecords.size()+" record(s)");

    }

    @Override
    public int getItemCount() {
        return singleDateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //initilase variable
        TextView textName, textView2;
        RecyclerView rv_member;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign variable
            textName = itemView.findViewById(R.id.date_of_test);
            rv_member = itemView.findViewById(R.id.rv_member);
            textView2 = itemView.findViewById(R.id.textView2);
        }
    }
}
