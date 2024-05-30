package com.isolate.egovdhn.in.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.GetSchedulesRequestModel;
import com.isolate.egovdhn.in.Models.ResponseGetPhoto;
import com.isolate.egovdhn.in.Models.ResponseUpload;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.FileUtils;
import com.isolate.egovdhn.in.Utilities.HelperClass;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoUploadActivity extends AppCompatActivity implements InternetConnectionListener {
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.buttonTakePic)
    Button buttonTakePic;
    @BindView(R.id.buttonUpload)
    Button buttonUpload;
    @BindView(R.id.textView4)
    TextView textView;
    @BindView(R.id.card_view)
    CardView card_view;
    @BindView(R.id.constraint_layout)
    ConstraintLayout constraintLayout;
    RetrofitClient retrofitClient;
    View parentLayout;

    private int REQUEST_CODE_FOR_IMAGE = -1;
    Uri finalUri = null;
    Unbinder unbinder;
    String currentPhotoPath, photo_id;
    AlertDialog alertDialogProgress;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1003;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1004;

    @Override
    protected void onDestroy() {
        if (unbinder != null) unbinder.unbind();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);
        parentLayout = findViewById(android.R.id.content);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Upload Photo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        checkEnable();

        buttonUpload.setEnabled(false);
        buttonUpload.setBackgroundColor(Color.GRAY);
        buttonTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(444);
            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable()) {
                    onInternetUnavailable();
                    return;
                }
                showProgressDialogue();
                Map<String, Object> mp = new HashMap<>();
                mp.put("srf_id", Prefs.getUser(PhotoUploadActivity.this).getSrfId());
                mp.put("photo_id", photo_id);

                Gson gson = new Gson();
                String fStatus = gson.toJson(mp);
                RequestBody infoPart = RequestBody.create(MultipartBody.FORM, fStatus);

                MultipartBody.Part image;
                ArrayList< MultipartBody.Part > fileWashroom = new ArrayList<>(), fileHome = new ArrayList<>();

                File originalFile;
                RequestBody filePart;

                originalFile = FileUtils.getFile(PhotoUploadActivity.this, finalUri);
                filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(finalUri)), originalFile);
                image = MultipartBody.Part.createFormData("photo", originalFile.getName(), filePart);
                upload(infoPart, image);
            }
        });

    }

    private void takePhoto(int requestCode) {
        REQUEST_CODE_FOR_IMAGE = requestCode;
        if (ContextCompat.checkSelfPermission(PhotoUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhotoUploadActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(PhotoUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhotoUploadActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            openCamera(requestCode);
        }
    }

    private void openCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            Uri tempPhotoUri = FileProvider.getUriForFile(this, "com.isolate.egovdhn.in.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri);
            startActivityForResult(intent, requestCode);
        }
        //startActivityForResult(intent, requestCode);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            File f = new File(currentPhotoPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);

            Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            Uri imageUri = HelperClass.getImageUri(PhotoUploadActivity.this, previewBitmap);

            Bitmap serverBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 768, true);
            Uri serverImageUri = HelperClass.getImageUri(PhotoUploadActivity.this, serverBitmap);

            if (f.exists()) {
                if (f.delete()) {
                    Log.d("PhotoUpload", "onActivityResult: File gone!");
                } else {
                    Log.d("PhotoUpload", "onActivityResult: File not gone!");
                }
            }

            finalUri = serverImageUri;
            Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_circle_24).into(imageView);
            buttonUpload.setEnabled(true);
            buttonUpload.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.upload_photo_bgbtn));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ask for write Permission
                if (ContextCompat.checkSelfPermission(PhotoUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoUploadActivity.this,
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return super.onSupportNavigateUp();
    }

    void checkEnable() {
        showProgressDialogue();
        RetrofitNetworkApi retrofitNetworkApi;
        retrofitNetworkApi = retrofitClient.getAPIService(this);
        Call< ResponseGetPhoto > call = retrofitNetworkApi.getPhotoEnable(new GetSchedulesRequestModel(Prefs.getUser(PhotoUploadActivity.this).getSrfId()));

        call.enqueue(new Callback< ResponseGetPhoto >() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call< ResponseGetPhoto > call, Response< ResponseGetPhoto > response) {
                alertDialogProgress.dismiss();
                Log.i("Photo_Api", response.message());
                if (response.isSuccessful() && response.body() != null && response.body().status == 200 && response.body().getIdModelList().size()>0) {
                    photo_id = response.body().getIdModelList().get(0).getPhoto_id();
                    textView.setVisibility(View.GONE);
                    card_view.setVisibility(View.VISIBLE);
                }
                else {
                    textView.setText("Please return later...");
                    textView.setVisibility(View.VISIBLE);
                    card_view.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call< ResponseGetPhoto > call, Throwable t) {
                Log.i("Photo_Api", t.getLocalizedMessage());
                alertDialogProgress.dismiss();
                finish();
            }
        });
    }

    private void showProgressDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoUploadActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.new_dialogue, null);
        builder.setView(view);
        alertDialogProgress = builder.create();
        alertDialogProgress.setCancelable(false);
        alertDialogProgress.show();
    }

    public void upload(RequestBody info, MultipartBody.Part image) {
        RetrofitNetworkApi retrofitNetworkApi;
        retrofitNetworkApi = retrofitClient.getAPIService(this);
        Call< ResponseUpload > call = retrofitNetworkApi.uploadPhoto(info, image);

        call.enqueue(new Callback< ResponseUpload >() {
            @Override
            public void onResponse(Call< ResponseUpload > call, Response< ResponseUpload > response) {
                alertDialogProgress.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    HelperClass.toast(PhotoUploadActivity.this,response.body().message);
                    finish();
                } else if (response.body() != null) {
                    HelperClass.toast(PhotoUploadActivity.this, "Failed to Upload \n" + response.body().message);
                }
            }

            @Override
            public void onFailure(Call<ResponseUpload> call, Throwable t) {
                alertDialogProgress.dismiss();
            }
        });
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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