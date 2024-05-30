package com.isolate.egovdhn.in.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.ResponseAddRecord;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.FileUtils;
import com.isolate.egovdhn.in.Utilities.HelperClass;
import com.isolate.egovdhn.in.Utilities.NotifyWorker;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, InternetConnectionListener {
    public static final int[] GET_HOME = {4, 5, 6, 7, 8};
    public static final String TAG = "HomeIsolation";
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1001;
    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 1002;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1003;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1004;
    public static final String workTag = "AlertAddRecord";

    @BindView(R.id.text_pulse)
    TextInputLayout text_pulse;
    @BindView(R.id.text_bph)
    TextInputLayout text_bph;
    @BindView(R.id.text_bpl)
    TextInputLayout text_bpl;
    @BindView(R.id.text_spo2)
    TextInputLayout text_spo2;
    @BindView(R.id.text_temp)
    TextInputLayout text_temp;
    @BindView(R.id.text_rate)
    TextInputLayout text_rate;
    @BindView(R.id.buttonPicLocation)
    Button buttonPicLocation;
    @BindView(R.id.image1)
    CircularImageView imageHome1;
    @BindView(R.id.image2)
    CircularImageView imageHome2;
    @BindView(R.id.image3)
    CircularImageView imageHome3;
    @BindView(R.id.input_Long)
    TextView textLong;
    @BindView(R.id.input_Lat)
    TextView textLat;
    @BindView(R.id.request_btn)
    Button btnApply;
    @BindView(R.id.temp_proof_btn)
    MaterialButton temp_proof_btn;
    @BindView(R.id.bp_proof_btn)
    MaterialButton bp_proof_btn;
    @BindView(R.id.spO2_proof_btn)
    MaterialButton spO2_proof_btn;
    @BindView(R.id.cancel_btn)
    Button cancel_btn;

    String currentPhotoPath;
    ArrayList<Uri> uriHome = new ArrayList<>();
    Gson gson;
    Unbinder unbinder;
    AlertDialog alertDialogProgress;
    double latitude, longitude;
    boolean isLocationKnown = false;
    private int REQUEST_CODE_FOR_IMAGE = -1;
    private int homePhotoCounter = 0;
    private Location currLocation = null;
    RetrofitClient retrofitClient;
    View parentLayout;

    @Override
    protected void onDestroy() {
        if (unbinder != null) unbinder.unbind();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_health_report);

        parentLayout = findViewById(android.R.id.content);
        unbinder = ButterKnife.bind(this);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);

        gson = new Gson();
        setSupportActionBar(findViewById(R.id.toolbar_add_report));
        getSupportActionBar().setTitle("Enter Your Details");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setOnClickListener();
        for (int i = 0; i < 3; i++)
            uriHome.add(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setOnClickListener() {
        btnApply.setOnClickListener(this);
        temp_proof_btn.setOnClickListener(this);
        bp_proof_btn.setOnClickListener(this);
        spO2_proof_btn.setOnClickListener(this);
        buttonPicLocation.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }

    private void searchForGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    public Location getLocation() {
        boolean isGpsEnabled = false;
        boolean isNetworkEnabled = false;
        Location mLocation = null;

        try {

            LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            /*getting status of the gps*/
            isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            /*getting status of network provider*/
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {
                //this.canGetLocation = false;
                HelperClass.toast(this, "No Location Provider Enabled");
                return null;
                /*no location provider enabled*/
            } else {

                /*getting location from network provider*/
                if (isNetworkEnabled) {
                    Log.d("network", "enabled");


                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }

                    Log.d("network", "permissions given");
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10, this);

                    if (mLocationManager != null) {

                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (mLocation != null) {

                            textLat.setText("" + mLocation.getLatitude());

                            textLong.setText("" + mLocation.getLongitude());
                        }
                    }
                    /*if gps is enabled then get location using gps*/
                    if (isGpsEnabled) {

                        if (mLocation == null) {

                            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, this);

                            if (mLocationManager != null) {

                                mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (mLocation != null) {

                                    textLat.setText("" + mLocation.getLatitude());

                                    textLong.setText("" + mLocation.getLongitude());
                                }

                            }
                        }

                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return currLocation = mLocation;
    }

    private void takePhoto(int requestCode) {
        REQUEST_CODE_FOR_IMAGE = requestCode;
        if (ContextCompat.checkSelfPermission(UpdateActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(UpdateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            openCamera(requestCode);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try{
            photoFile = createImageFile();
        }catch(IOException ex){
            ex.printStackTrace();
        }

        if(photoFile!=null){
            Uri tempPhotoUri = FileProvider.getUriForFile(this, "com.isolate.egovdhn.in.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri);
            startActivityForResult(intent, requestCode);
        }
        //startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
        if (resultCode == RESULT_OK) {
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Uri imageUri = HelperClass.getImageUri(UpdateActivity.this, imageBitmap);*/

            File f = new File(currentPhotoPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);

            Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            Uri imageUri = HelperClass.getImageUri(UpdateActivity.this, previewBitmap);

            Bitmap serverBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 768, true);
            Uri serverImageUri = HelperClass.getImageUri(UpdateActivity.this, serverBitmap);

            if(f.exists()){
                if(f.delete()){
                    Log.d("SignUp", "onActivityResult: File gone!");
                }else{
                    Log.d("SignUp", "onActivityResult: File not gone!");
                }
            }

            if (true) {
                for (int i = 0; i < 5; i++) {
                    if (requestCode == GET_HOME[i]) {
                        //uriHome.set(i, imageUri);
                        uriHome.set(i, serverImageUri);
                        homePhotoCounter++;
                        if (homePhotoCounter >= 5) homePhotoCounter = 0;
                        if (i == 0)
                            Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_circle_24).into(imageHome1);
                        else if (i == 1)
                            Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_circle_24).into(imageHome2);
                        else if (i == 2)
                            Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_circle_24).into(imageHome3);
                        //File originalFile = FileUtils.getFile(this, uriHome.get(i));
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ask for write Permission
                if (ContextCompat.checkSelfPermission(UpdateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            } else {
                HelperClass.toast(this, "Read Permissions Denied\nPlease Give Permissions");
            }
        }

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ask for Camera option Permission.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                } else {
                    openCamera(requestCode);
                }
            } else {
                HelperClass.toast(this, "Write Permissions Denied\nPlease Give Permissions");
            }
        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera(REQUEST_CODE_FOR_IMAGE);
            } else {
                HelperClass.toast(this, "Camera Permissions Denied\nPlease Give Permissions");
            }
        }

        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted Fine location
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
                } else {
                    getLocation();
                }
            } else {
                HelperClass.toast(this, "Permissions Denied\nPlease Give Permissions");
            }
        }

        if (requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                HelperClass.toast(this, "Permissions Denied\nPlease Give Permissions");
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.request_btn:
                if (isValid())
                    sendIsolationRequest();
                break;
            case R.id.temp_proof_btn:
                takePhoto(GET_HOME[0]);
                break;
            case R.id.bp_proof_btn:
                takePhoto(GET_HOME[1]);
                break;
            case R.id.spO2_proof_btn:
                takePhoto(GET_HOME[2]);
                break;
            case R.id.cancel_btn:
                finish();
                break;
            case R.id.buttonPicLocation:
                searchForGPS();
                break;
        }
    }

    private boolean isValid() {
        String pulse = text_pulse.getEditText().getText().toString().trim();
        String temp = text_temp.getEditText().getText().toString().trim();
        String bph = text_bph.getEditText().getText().toString().trim();
        String bpl = text_bpl.getEditText().getText().toString().trim();
        String spo2 = text_spo2.getEditText().getText().toString().trim();
        String rate = text_rate.getEditText().getText().toString().trim();

        if (pulse.isEmpty() || temp.isEmpty() || bph.isEmpty() || bpl.isEmpty() || spo2.isEmpty() || rate.isEmpty()) {
            Toast.makeText(this, "Please fill up the fields!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (uriHome == null || uriHome.size() < 3 || uriHome.get(0) ==null || uriHome.get(1) ==null || uriHome.get(2) ==null ) {
            HelperClass.toast(this, "Please submit all three Proofs");
            return false;
        }
        /*if (!isLocationKnown) {
            HelperClass.toast(this, "Please Provide your location by clicking PicLocation Button");
            return false;
        }*/
        if (currLocation == null) {
            HelperClass.toast(this, "Please Provide your location by clicking PicLocation Button");
            return false;
        }

        return true;
    }

    private void sendIsolationRequest() {
        btnApply.setEnabled(false);
        String pulse = text_pulse.getEditText().getText().toString().trim();
        String temp = text_temp.getEditText().getText().toString().trim();
        String bph = text_bph.getEditText().getText().toString().trim();
        String bpl = text_bpl.getEditText().getText().toString().trim();
        String spo2 = text_spo2.getEditText().getText().toString().trim();
        String rate = text_rate.getEditText().getText().toString().trim();

        Map<String, Object> mp = new HashMap<>();
        if (Prefs.getUser(UpdateActivity.this) != null && Prefs.getUser(UpdateActivity.this).getSrfId() != null)
            mp.put("srf_id", Prefs.getUser(UpdateActivity.this).getSrfId());
        else {
            HelperClass.toast(this, "Users srf id not found\n Please logout and login again");
        }
        mp.put("pulse_rate", Double.valueOf(pulse));
        mp.put("temperature", Double.valueOf(temp));
        mp.put("bp_high", Double.valueOf(bph));
        mp.put("bp_low", Double.valueOf(bpl));
        mp.put("spo2", Double.valueOf(spo2));
        mp.put("respiratory_rate", Double.valueOf(rate));
        mp.put("latitude", currLocation.getLatitude());
        mp.put("longitude", currLocation.getLongitude());

        String fStatus = gson.toJson(mp);
        RequestBody infoPart = RequestBody.create(MultipartBody.FORM, fStatus);

        ArrayList<MultipartBody.Part> fileHome = new ArrayList<>();

        File originalFile;
        RequestBody filePart;

        for (int i = 0; i < 3; i++) {
            originalFile = FileUtils.getFile(this, uriHome.get(i));
            Log.d(TAG, "Home Uri - " + uriHome.get(i));
            filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(uriHome.get(i))), originalFile);
            if (i == 0)
                fileHome.add(MultipartBody.Part.createFormData("temperatureProof", originalFile.getName(), filePart));
            else if (i == 1)
                fileHome.add(MultipartBody.Part.createFormData("bpProof", originalFile.getName(), filePart));
            else
                fileHome.add(MultipartBody.Part.createFormData("spo2Proof", originalFile.getName(), filePart));
        }

        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        Call<ResponseAddRecord> call = retrofitNetworkApi.updateData(
                infoPart, fileHome.get(0), fileHome.get(1), fileHome.get(2));

        showProgressDialogue();

        call.enqueue(new Callback<ResponseAddRecord>() {
            @Override
            public void onResponse(Call<ResponseAddRecord> call, Response<ResponseAddRecord> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HelperClass.toast(UpdateActivity.this, response.body().message);
                    if(response.body().status == 200) {
                        //buildNotificationRequest();
                        finish();
                    }
                } else if (response.body() != null) {
                    HelperClass.toast(UpdateActivity.this, "Failed to Upload \n" + response.body().message);
                }
                alertDialogProgress.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseAddRecord> call, Throwable t) {
                alertDialogProgress.dismiss();
            }
        });
    }

    private void showProgressDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.new_dialogue, null);
        builder.setView(view);
//        TextView title = view.findViewById(R.id.textViewTitle);
//        title.setText("Please Wait...");

        alertDialogProgress = builder.create();
        alertDialogProgress.setCancelable(false);
        alertDialogProgress.show();
    }

    /*private void buildNotificationRequest(){
        HelperClass.cancelAllNotifications(getApplicationContext());

        Data inputData = new Data.Builder().putInt("Home", 1).build();

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                .setInitialDelay(3, TimeUnit.HOURS)
                .setInputData(inputData)
                .addTag(NotifyWorker.TAG)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(notificationWork);

        finish();
    }*/

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currLocation = location;
        if (currLocation != null) {
            textLat.setText("" + currLocation.getLatitude());
            textLong.setText("" + currLocation.getLongitude());
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
}