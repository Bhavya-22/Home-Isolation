package com.isolate.egovdhn.in.UI;

import android.Manifest;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.isolate.egovdhn.in.Adapter.DateAdapter;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.ChangePasswordRequestModel;
import com.isolate.egovdhn.in.Models.GetRecordsRequestModel;
import com.isolate.egovdhn.in.Models.HealthRecordModel;
import com.isolate.egovdhn.in.Models.RecordsDateModel;
import com.isolate.egovdhn.in.Models.ResponseChangePassword;
import com.isolate.egovdhn.in.Models.ResponseGetRecords;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.HelperClass;
import com.isolate.egovdhn.in.Utilities.LocationService;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements InternetConnectionListener {
    private static final String TAG = "MainActivity";
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1001;
    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 1002;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1003;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1004;

    RecyclerView recyclerView;
    ImageView noRecords;
    ArrayList<String> arrayListDate;
    LinearLayoutManager layoutManagerDate;
    DateAdapter adapter;
    ArrayList<RecordsDateModel> segregatedRecords;
    NavigationView navigationView;
    FloatingActionButton add_report_btn;
    DrawerLayout drawer;
    RetrofitClient retrofitClient;
    View parentLayout;

    private final NavigationView.OnNavigationItemSelectedListener navListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    Intent intent;
                    switch (id) {

                        case R.id.prescriptions:
                            //Add prescriptions activity
                            intent = new Intent(MainActivity.this, PrescriptionActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.uploads:
                            //Add uploads activity
                            intent = new Intent(MainActivity.this, UploadActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.photo_upload:
                            //Add Photo Upload activity
                            intent = new Intent(MainActivity.this, PhotoUploadActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.changePassword:
                            //To give option to change password
                            showChangePassword();
                            break;
                        case R.id.telemedicine:
                            intent = new Intent(MainActivity.this, TelemedicineActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.controlRoom:
                            intent = new Intent(MainActivity.this, ContactActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.advisory:
                            //intent = new Intent(MainActivity.this, GuidelinesActivity.class);
                            //startActivity(intent);
                            String url = "isolate.egovdhn.in/advisory";
                            if (!url.startsWith("http://") && !url.startsWith("https://"))
                                url = "http://" + url;

                            Uri uri = Uri.parse(url);
                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                            break;
                        case R.id.logout:
                            intent = new Intent(MainActivity.this, SignInActivity.class);
                            Prefs.setUserLoggedIn(MainActivity.this, false);
                            Prefs.setToken(MainActivity.this, null);
                            Prefs.SetUserData(MainActivity.this, null);
                            HelperClass.cancelAllNotifications(getApplicationContext());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            stopLocationService();
                            break;
                    }
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }

            };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.notification)
        {
            Intent notifIntent = new Intent(getApplicationContext(),NotificationActivity.class);
            startActivity(notifIntent);
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private void segregateRecords(ArrayList<HealthRecordModel> totalRecords) {
        Collections.reverse(totalRecords);
        if(totalRecords.size() == 0){
            noRecords.setVisibility(View.VISIBLE);
        }
        segregatedRecords = new ArrayList<>();

        for (HealthRecordModel model : totalRecords) {
            try {
                String modelDate = HelperClass.dateFormatter(HelperClass.serverDateFormat().parse(model.timestamp));

                boolean found = false;

                for (int i = 0; i < segregatedRecords.size(); i++) {
                    String existingDate = HelperClass.dateFormatter(segregatedRecords.get(i).date);
                    if (existingDate.equals(modelDate)) {
                        found = true;
                        segregatedRecords.get(i).singleDateRecords.add(model);
                        break;
                    }
                }

                if(!found){
                    RecordsDateModel recordsDateModel = new RecordsDateModel(HelperClass.serverDateFormat().parse(model.timestamp));
                    recordsDateModel.singleDateRecords.add(model);
                    segregatedRecords.add(recordsDateModel);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        populateRecyclerView();
    }

    private void populateRecyclerView(){
        //Initiliase group adapter
        adapter= new DateAdapter(MainActivity.this, segregatedRecords);
        //set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        //set adapter
        recyclerView.setAdapter(adapter);
    }

    private void showChangePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.change_password_dialog, null);
        TextInputLayout old_password = view.findViewById(R.id.old_password);
        TextInputLayout new_password = view.findViewById(R.id.new_password);

        builder.setView(view)
                .setTitle("Change Password")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldPassword = old_password.getEditText().getText().toString().trim();
                        String newPassword = new_password.getEditText().getText().toString().trim();

                        if(oldPassword.isEmpty()){
                            old_password.setError("Please enter old password.");
                            return;
                        }else{
                            old_password.setError(null);
                        }

                        if(newPassword.isEmpty()){
                            new_password.setError("Please enter new password.");
                            return;
                        }else{
                            new_password.setError(null);
                        }

                        builder.setCancelable(false);
                        ChangePasswordRequestModel model = new ChangePasswordRequestModel(Prefs.getUser(MainActivity.this).getSrfId(), oldPassword, newPassword);
                        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(MainActivity.this);
                        retrofitNetworkApi.changePassword(model).enqueue(new Callback<ResponseChangePassword>() {
                            @Override
                            public void onResponse(Call<ResponseChangePassword> call, Response<ResponseChangePassword> response) {
                                builder.setCancelable(true);
                                if(response.isSuccessful() && response.body() != null){
                                    HelperClass.toast(MainActivity.this, response.body().message);
                                }else{
                                    Log.d(TAG, "onResponse: " + response.errorBody().toString());
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ResponseChangePassword> call, Throwable t) {
                                builder.setCancelable(true);
                                Log.d(TAG, "onFailure: Failed to send request." + t.getLocalizedMessage());
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_main);
        parentLayout = findViewById(android.R.id.content);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar_heath_records);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Health Records");
        //Assign variable
        noRecords = findViewById(R.id.imageView3);
        recyclerView = findViewById(R.id.home_rec_view);
        add_report_btn = findViewById(R.id.add_report_btn);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navListener);

        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.changePassword, R.id.controlRoom, R.id.telemedicine, R.id.advisory, R.id.logout)
                .setDrawerLayout(drawer)
                .build();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        add_report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UpdateActivity.class));
            }
        });


        requestPermissions();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //If this is the case we want to close it
            drawer.closeDrawer(GravityCompat.START);
        } else { //This implies drawer is not open
            super.onBackPressed();  //In this case close the activity as usual
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icon, menu);
        return true;
    }

    boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) return true;
                }
            }
            return false;
        }
        return false;
    }

    public void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(this, LocationService.class);
            intent.setAction(HelperClass.ACTION_START_LOCATION_SERVICE);
            startService(intent);
        }
    }

    public void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(this, LocationService.class);
            intent.setAction(HelperClass.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
        }
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
        } else {
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ask for other Permission
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
                } else {
                    startLocationService();
                }
            } else {
                HelperClass.toast(this, "Read Permissions Denied\nPlease Give Permissions");
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            }
        }

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ask for Camera option Permission.
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
                } else {
                    startLocationService();
                }
            } else {
                HelperClass.toast(this, "Write Permissions Denied\nPlease Give Permissions");
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            }
        }

        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted Fine location
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
                } else {
                    startLocationService();
                }
            } else {
                HelperClass.toast(this, "Permissions Denied\nPlease Give Permissions");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
                }
            }
        }

        if (requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                HelperClass.toast(this, "Permissions Denied\nPlease Give Permissions");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
                }
            }
        }
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
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        GetRecordsRequestModel model = new GetRecordsRequestModel(Prefs.getUser(this).getSrfId());
        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        retrofitNetworkApi.getRecords(model).enqueue(new Callback<ResponseGetRecords>() {
            @Override
            public void onResponse(Call<ResponseGetRecords> call, Response<ResponseGetRecords> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status == 200) {
                    segregateRecords(response.body().records);
                } else if (response.body() != null && response.body().message != null) {
                    HelperClass.toast(MainActivity.this, response.body().message);
                }
            }

            @Override
            public void onFailure(Call<ResponseGetRecords> call, Throwable t) {
            }
        });
    }
}