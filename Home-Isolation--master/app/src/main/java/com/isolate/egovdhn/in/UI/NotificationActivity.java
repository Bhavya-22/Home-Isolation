package com.isolate.egovdhn.in.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.isolate.egovdhn.in.Adapter.NotifAdapter;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.GetPrescriptionRequestModel;
import com.isolate.egovdhn.in.Models.NotifModel;
import com.isolate.egovdhn.in.Models.NotifResponse;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity implements NotifAdapter.NotifListener, InternetConnectionListener {

    RecyclerView notificationRecyclerView;
    NotifAdapter adapter;
    TextView textView;
    ArrayList<NotifModel> notifications = new ArrayList<>();
    RetrofitClient retrofitClient;
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        parentLayout = findViewById(android.R.id.content);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);

        setSupportActionBar(findViewById(R.id.toolbar_not));
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView = findViewById(R.id.textView6);
        getData();
    }

    private void getData(){
        GetPrescriptionRequestModel model = new GetPrescriptionRequestModel(Prefs.getUser(this).getSrfId());
        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        retrofitNetworkApi.getNotifications(model).enqueue(new Callback< NotifResponse >() {
            @Override
            public void onResponse(Call<NotifResponse> call, Response<NotifResponse> response) {
                Log.i("Noti_API_Message", response.message());
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    notifications = response.body().getNotifModels();
                    populateRecyclerView();
                } else if (response.body() != null && response.body().getMessage() != null) {
                    HelperClass.toast(NotificationActivity.this, response.body().getMessage());
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<NotifResponse> call, Throwable t) {
            }
        });
    }
    private void populateRecyclerView(){
        if(notifications.size()==0){
            textView.setVisibility(View.VISIBLE);
        }
        Collections.reverse(notifications);
        notificationRecyclerView = findViewById(R.id.not_recycler);
        adapter = new NotifAdapter(this,notifications);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        notificationRecyclerView.setAdapter(adapter);
        notificationRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(int itemPosition) {

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