package com.isolate.egovdhn.in.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.isolate.egovdhn.in.Adapter.TelemedicineScheduleAdapter;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.GetSchedulesRequestModel;
import com.isolate.egovdhn.in.Models.ResponseGetSchedules;
import com.isolate.egovdhn.in.Models.ResponseResetPassword;
import com.isolate.egovdhn.in.Models.TelemedicineScheduleModel;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelemedicineActivity extends AppCompatActivity implements InternetConnectionListener, TelemedicineScheduleAdapter.TelemedicineScheduleListener {

    RecyclerView scheduleRecyclerView;
    TextView textView;
    TelemedicineScheduleAdapter adapter;
    ArrayList<TelemedicineScheduleModel> schedules;
    RetrofitClient retrofitClient;
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tele_medicine);
        parentLayout = findViewById(android.R.id.content);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);

        setSupportActionBar(findViewById(R.id.toolbar_telemedicine));
        getSupportActionBar().setTitle("Telemedicine");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView = findViewById(R.id.textView6);

        scheduleRecyclerView = findViewById(R.id.appt_recycler);

        GetSchedulesRequestModel model = new GetSchedulesRequestModel(Prefs.getUser(this).getSrfId());
        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        retrofitNetworkApi.getSchedules(model).enqueue(new Callback<ResponseGetSchedules>() {
            @Override
            public void onResponse(Call<ResponseGetSchedules> call, Response<ResponseGetSchedules> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status == 200) {
                    schedules = response.body().schedules;
                    populateRecyclerView();
                } else if (response.body() != null && response.body().message != null) {
                    HelperClass.toast(TelemedicineActivity.this, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<ResponseGetSchedules> call, Throwable t) {
            }
        });
    }

    private void populateRecyclerView() {
        if(schedules.size()==0){
            textView.setVisibility(View.VISIBLE);
        }
        adapter = new TelemedicineScheduleAdapter(this, schedules);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        scheduleRecyclerView.setAdapter(adapter);
        scheduleRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(int itemPosition) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tele_id", schedules.get(itemPosition).telemedicine_id);

        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        retrofitNetworkApi.setStatus(map).enqueue(new Callback<ResponseResetPassword>() {
            @Override
            public void onResponse(Call<ResponseResetPassword> call, Response<ResponseResetPassword> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status == 200) {
                    openPage(itemPosition);
                } else if (response.body() != null && response.body().message != null) {
                    HelperClass.toast(TelemedicineActivity.this, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<ResponseResetPassword> call, Throwable t) {
            }
        });
    }

    private void openPage(int itemPosition){
        try {
            String url = schedules.get(itemPosition).link;
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