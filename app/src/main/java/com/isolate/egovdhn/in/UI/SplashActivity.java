package com.isolate.egovdhn.in.UI;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.FCMTokenRequest;
import com.isolate.egovdhn.in.Models.GetSchedulesRequestModel;
import com.isolate.egovdhn.in.Models.ResponseResetPassword;
import com.isolate.egovdhn.in.Models.ResponseUpload;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.HelperClass;
import com.isolate.egovdhn.in.Utilities.LocationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1001;
    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 1002;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1003;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1004;

    RetrofitClient retrofitClient;
    boolean isConnected = true;
    boolean monitoringConnectivity = false;
    View parentLayout;
    private final ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            Snackbar.make(parentLayout, "Connected to Internet. Back Online!!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(android.R.color.holo_blue_bright))
                    .show();
            isConnected = true;
            takeAction();
        }

        @Override
        public void onLost(Network network) {
            Snackbar.make(parentLayout, "No Internet Connection Available", Snackbar.LENGTH_LONG)
                    .show();
            isConnected = false;
        }
    };

    @Override
    protected void onPause() {
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        parentLayout = findViewById(android.R.id.content);

        retrofitClient = ((RetrofitClient) getApplication());
        startJob(parentLayout);
    }

    private void startJob(View parentLayout) {
        if (!retrofitClient.isInternetAvailable()) {
            Snackbar.make(parentLayout, "No Internet Connection Available", Snackbar.LENGTH_LONG)
                    .show();
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
        } else {
            takeAction();
        }
    }

    private void takeAction() {
        if (Prefs.isUserLoggedIn(this) && Prefs.getUser(this)!=null) {
            checkLoginValid();
            getFCMToken();
        } else {
            clearPrefAndLogout();
        }
    }

    private void checkLoginValid() {
        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        retrofitNetworkApi.getUserActive(new GetSchedulesRequestModel(Prefs.getUser(this).getSrfId())).enqueue(new Callback< ResponseResetPassword >() {
            @Override
            public void onResponse(Call<ResponseResetPassword> call, Response<ResponseResetPassword> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status == 200) {
                    gotoMain();
                }
                else {
                    Log.i("Message", response.message());
                    HelperClass.toast(SplashActivity.this, "User logged out \n("+response.body().message+ ")");
                    clearPrefAndLogout();
                }
            }

            @Override
            public void onFailure(Call<ResponseResetPassword> call, Throwable t) {
                HelperClass.toast(SplashActivity.this, "User logged out \n("+t.getLocalizedMessage()+ ")");
                clearPrefAndLogout();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ask for other Permission
                if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SplashActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
                } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
                } else {
                    takeAction();
                }
            } else {
                HelperClass.toast(this, "Read Permissions Denied\nPlease Give Permissions");
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
                    takeAction();
                }
            } else {
                HelperClass.toast(this, "Write Permissions Denied\nPlease Give Permissions");
            }
        }

        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted Fine location
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
                } else {
                    takeAction();
                }
            } else {
                HelperClass.toast(this, "Permissions Denied\nPlease Give Permissions");
            }
        }

        if (requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeAction();
            } else {
                HelperClass.toast(this, "Permissions Denied\nPlease Give Permissions");
            }
        }
    }

    private void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Task : ", "Hi getInstanceId failed * " + task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.d("FCM_TOKEN", token);
                        saveTokenToServer(token);
                    }
                });

    }

    private void saveTokenToServer(String token) {
        FCMTokenRequest request = new FCMTokenRequest(Prefs.getUser(this).getSrfId(), token);
        RetrofitNetworkApi networkApi = retrofitClient.getAPIService(this);
        networkApi.saveTokenToServer(request).enqueue(new Callback<ResponseUpload>() {
            @Override
            public void onResponse(Call<ResponseUpload> call, Response<ResponseUpload> response) {
                if (response != null && response.body() != null && response.isSuccessful() && response.body().status == 200) {
                    // Token Successfully Saved to server
                }
            }

            @Override
            public void onFailure(Call<ResponseUpload> call, Throwable t) {

            }
        });
    }

    private void clearPrefAndLogout(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Prefs.setUserLoggedIn(SplashActivity.this, false);
                Prefs.setToken(SplashActivity.this, null);
                Prefs.SetUserData(SplashActivity.this, null);
                HelperClass.cancelAllNotifications(getApplicationContext());
                Intent signInIntent = new Intent(getApplication(), SignInActivity.class);
                signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(signInIntent);
                stopLocationService();
                finish();
            }
        }, 2000);
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

    public void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(this, LocationService.class);
            intent.setAction(HelperClass.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
        }
    }

    private void gotoMain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent signInIntent = new Intent(getApplication(), MainActivity.class);
                signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(signInIntent);
                finish();
            }
        }, 2000);
    }
}