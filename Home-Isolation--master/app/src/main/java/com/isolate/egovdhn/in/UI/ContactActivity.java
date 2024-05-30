package com.isolate.egovdhn.in.UI;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.isolate.egovdhn.in.Adapter.EmergencyContactAdapter;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.AddAlertRequestModel;
import com.isolate.egovdhn.in.Models.ResponseAddAlert;
import com.isolate.egovdhn.in.Models.ResponseGetContacts;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends AppCompatActivity implements InternetConnectionListener, EmergencyContactAdapter.ContactListener {

    TextInputEditText alert_message;
    Button sos_btn;
    TashieLoader progressLoader;
    //TextView phoneNo;
    RecyclerView contactsRecyclerView;
    EmergencyContactAdapter adapter;
    ArrayList<ResponseGetContacts.EmergencyContact> contacts = new ArrayList<>();
    String selectedNumber = "";

    RetrofitClient retrofitClient;
    View parentLayout;

    private static final String TAG = "ContactActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        parentLayout = findViewById(android.R.id.content);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);
        alert_message = findViewById(R.id.alert_message);
        sos_btn = findViewById(R.id.sos_btn);
        progressLoader = findViewById(R.id.progressLoader);

        setSupportActionBar(findViewById(R.id.toolbar_controlRoom));
        getSupportActionBar().setTitle("Control Room");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alert_message.getText().toString() == null || alert_message.getText().toString().trim().equals("")) {
                    alert_message.setError("Please enter a description.");
                    return;
                }

                sos_btn.setEnabled(false);

                String description = alert_message.getText().toString().trim();
                AddAlertRequestModel model = new AddAlertRequestModel(Prefs.getUser(ContactActivity.this).getSrfId(), description);
                RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(ContactActivity.this);

                AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(ContactActivity.this);

                mAlertDialog.setTitle("Confirm Send");

                mAlertDialog.setMessage("Are you sure you want to send this message?");

                mAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressLoader.setVisibility(View.VISIBLE);
                        retrofitNetworkApi.addAlert(model).enqueue(new Callback<ResponseAddAlert>() {
                            @Override
                            public void onResponse(Call<ResponseAddAlert> call, Response<ResponseAddAlert> response) {
                                progressLoader.setVisibility(View.INVISIBLE);
                                if (response.body() != null && response.body().message != null) {
                                    HelperClass.toast(ContactActivity.this, response.body().message);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseAddAlert> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getMessage());
                                progressLoader.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });

                mAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog mCreateDialog = mAlertDialog.create();
                mCreateDialog.show();

            }
        });

        getData();
    }

    private void getData(){
        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        retrofitNetworkApi.getEmergencyContacts().enqueue(new Callback<ResponseGetContacts>() {
            @Override
            public void onResponse(Call<ResponseGetContacts> call, Response<ResponseGetContacts> response) {
                Log.i("Contacts_API_Message", response.message());
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    contacts = response.body().getContacts();
                    populateRecyclerView();
                } else if (response.body() != null && response.body().getMessage() != null) {
                    HelperClass.toast(ContactActivity.this, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetContacts> call, Throwable t) {

            }
        });
    }

    private void populateRecyclerView(){
        contactsRecyclerView = findViewById(R.id.recyclerViewContacts);
        adapter = new EmergencyContactAdapter(this, contacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        contactsRecyclerView.setAdapter(adapter);
        contactsRecyclerView.setHasFixedSize(true);
    }

    private void callToThePerson() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + selectedNumber));
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Log.e("Calling a Phone Number", "Call failed", activityException);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callToThePerson();
            } else {
                HelperClass.toast(this, "Permissions Denied\nPlease Give Permissions");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        Log.i(TAG, "onInternetUnavailable: called");
    }

    @Override
    public void onCacheUnavailable() {
        Log.i(TAG, "onCacheUnavailable: called");
    }

    @Override
    protected void onPause() {
        retrofitClient.removeInternetConnectionListener();
        super.onPause();
    }

    @Override
    public void onClick(int itemPosition) {
        if (ActivityCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(ContactActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                    10);
            return;
        }
        selectedNumber = contacts.get(itemPosition).getNumber();
        callToThePerson();
    }
}