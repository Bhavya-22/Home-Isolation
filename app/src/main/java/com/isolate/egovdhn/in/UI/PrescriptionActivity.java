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
import com.isolate.egovdhn.in.Adapter.PrescriptionAdapter;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.GetPrescriptionRequestModel;
import com.isolate.egovdhn.in.Models.PrescriptionModel;
import com.isolate.egovdhn.in.Models.ResponseGetPrescription;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrescriptionActivity extends AppCompatActivity implements PrescriptionAdapter.PrescriptionListener, InternetConnectionListener {

    RecyclerView prescriptionRecyclerView;
    PrescriptionAdapter adapter;
    TextView textView;
    ArrayList<PrescriptionModel> prescriptions;
    RetrofitClient retrofitClient;
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        parentLayout = findViewById(android.R.id.content);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);

        setSupportActionBar(findViewById(R.id.toolbar_prescription));
        getSupportActionBar().setTitle("Prescriptions");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView = findViewById(R.id.textView6);

        prescriptionRecyclerView = findViewById(R.id.appt_recycler);

        GetPrescriptionRequestModel model = new GetPrescriptionRequestModel(Prefs.getUser(this).getSrfId());
        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        retrofitNetworkApi.getPrescription(model).enqueue(new Callback<ResponseGetPrescription>() {
            @Override
            public void onResponse(Call<ResponseGetPrescription> call, Response<ResponseGetPrescription> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status == 200) {
                    prescriptions = response.body().prescriptions;
                    populateRecyclerView();
                } else if (response.body() != null && response.body().message != null) {
                    HelperClass.toast(PrescriptionActivity.this, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPrescription> call, Throwable t) {
            }
        });
    }

    private void populateRecyclerView() {
        if(prescriptions.size()==0){
            textView.setVisibility(View.VISIBLE);
        }
        Collections.reverse(prescriptions);
        adapter = new PrescriptionAdapter(this, prescriptions);
        prescriptionRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        prescriptionRecyclerView.setAdapter(adapter);
        prescriptionRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(int itemPosition) {
        try {
            String url = prescriptions.get(itemPosition).prescription;
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