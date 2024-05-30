package com.isolate.egovdhn.in.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isolate.egovdhn.in.Models.PrescriptionModel;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    PrescriptionListener listener;
    ArrayList<PrescriptionModel> prescriptions;

    public PrescriptionAdapter(PrescriptionListener listener, ArrayList<PrescriptionModel> prescriptions) {
        this.listener = listener;
        this.prescriptions = prescriptions;
    }

    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PrescriptionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pres_card, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder holder, int position) {
        //holder.prescription_id.setText("Prescription ID : " + prescriptions.get(position).prescription_id);
        //holder.patient_id.setText("Patient ID : " + prescriptions.get(position).patient_id);
        //holder.remarks.setText("Patient ID : " + prescriptions.get(position).remarks);

        try {
            Date source = HelperClass.serverDateFormat2().parse(prescriptions.get(position).date);
            holder.date_check.setText("Date: " + HelperClass.dateFormatter2(source));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    public class PrescriptionViewHolder extends RecyclerView.ViewHolder{

        //TextView prescription_id, patient_id, date_check, remarks, prescription;
        TextView date_check, prescription;
        PrescriptionListener listener;

        public PrescriptionViewHolder(@NonNull View itemView, PrescriptionListener listener) {
            super(itemView);
            this.listener = listener;
            date_check = itemView.findViewById(R.id.date_text);
            prescription = itemView.findViewById(R.id.prescription_link_text);

            prescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrescriptionViewHolder.this.listener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public interface PrescriptionListener{
        void onClick(int itemPosition);
    }
}
