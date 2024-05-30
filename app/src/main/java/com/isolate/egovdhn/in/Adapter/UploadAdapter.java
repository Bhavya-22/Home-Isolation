package com.isolate.egovdhn.in.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isolate.egovdhn.in.Models.UploadModel;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.UploadViewHolder> {
    ArrayList< UploadModel > uploads;
    UploadListener listener;
    public UploadAdapter(UploadListener listener,ArrayList< UploadModel > uploads) {

        this.listener = listener;
        this.uploads = uploads;
    }


    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_card, parent, false);
        return new UploadViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadViewHolder holder, int position) {
        holder.title.setText(uploads.get(position).getType());

       try {
            Date source = HelperClass.serverDateFormat().parse(uploads.get(position).getTimestamp());
            holder.app_date.setText("Date: " + HelperClass.dateFormatter(source));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class UploadViewHolder extends RecyclerView.ViewHolder{

      public TextView title;
      public TextView app_date;
      public TextView doc;
      UploadListener listener;
        public UploadViewHolder(@NonNull View itemView,UploadListener listener) {
            super(itemView);
            this.listener = listener;
            title= itemView.findViewById(R.id.title);
            app_date=itemView.findViewById(R.id.doc_date_text);
            doc=itemView.findViewById(R.id.docum_link_text);
            doc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UploadViewHolder.this.listener.onClick(getAdapterPosition());
                }
            });
        }
    }
    public interface UploadListener{
        void onClick(int itemPosition);
    }
}
