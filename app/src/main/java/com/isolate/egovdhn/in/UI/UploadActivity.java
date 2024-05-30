package com.isolate.egovdhn.in.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.isolate.egovdhn.in.Adapter.UploadAdapter;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.GetPrescriptionRequestModel;
import com.isolate.egovdhn.in.Models.UploadModel;
import com.isolate.egovdhn.in.Models.UploadResponse;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity implements UploadAdapter.UploadListener, InternetConnectionListener {

    RecyclerView uploadsRecyclerView;
    UploadAdapter adapter;
    ArrayList<UploadModel> uploads = new ArrayList<>();
    RetrofitClient retrofitClient;
    View parentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        parentLayout = findViewById(android.R.id.content);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);

        setSupportActionBar(findViewById(R.id.toolbar_uploads));
        getSupportActionBar().setTitle("Uploads");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getData();
    }

    private void getData(){
        GetPrescriptionRequestModel model = new GetPrescriptionRequestModel(Prefs.getUser(this).getSrfId());
        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        retrofitNetworkApi.getUpload(model).enqueue(new Callback< UploadResponse >() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    uploads = (ArrayList< UploadModel >) response.body().getUploads();
                    populateRecyclerView();
                } else if (response.body() != null && response.body().getMessage() != null) {
                    HelperClass.toast(UploadActivity.this, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
            }
        });
    }
    private void populateRecyclerView() {
        TextView textView = findViewById(R.id.textView6);
        if(uploads.size()==0){
            textView.setVisibility(View.VISIBLE);
        }
        Collections.reverse(uploads);
        uploadsRecyclerView =findViewById(R.id.appt_recycler);

        adapter = new UploadAdapter(this, uploads);
        uploadsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        uploadsRecyclerView.setAdapter(adapter);
        uploadsRecyclerView.setHasFixedSize(true);
    }
    @Override
    public void onClick(int itemPosition) {
        try {
            String url = uploads.get(itemPosition).getPath();
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;

            Uri uri = Uri.parse(url);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "URL invalid. " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onInternetUnavailable() {
        Snackbar.make(parentLayout, "No Internet Connection Available", Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onCacheUnavailable() {
    }

    @Override
    protected void onPause() {
        retrofitClient.removeInternetConnectionListener();
        super.onPause();
    }
}
