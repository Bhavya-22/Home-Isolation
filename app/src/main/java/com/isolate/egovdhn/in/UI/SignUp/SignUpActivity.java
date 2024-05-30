package com.isolate.egovdhn.in.UI.SignUp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.BlockModel;
import com.isolate.egovdhn.in.Models.BlockResponse;
import com.isolate.egovdhn.in.Models.DistrictModel;
import com.isolate.egovdhn.in.Models.DistrictResponse;
import com.isolate.egovdhn.in.Models.StateModel;
import com.isolate.egovdhn.in.Models.StateResponse;
import com.isolate.egovdhn.in.Models.User;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.UI.SignInActivity;
import com.isolate.egovdhn.in.Utilities.FileUtils;
import com.isolate.egovdhn.in.Utilities.HelperClass;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, InternetConnectionListener, LocationListener, Contract.View {
    public static final int GET_AADHAAR = 1, GET_AADHAAR_BACK = 9;
    public static final int[] GET_WASHROOM = {2, 3};
    public static final int[] GET_HOME = {4, 5, 6, 7, 8};
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1001;
    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 1002;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1003;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1004;

    @BindView(R.id.srfId)
    TextInputLayout srfID;
    @BindView(R.id.dateOfTest)
    DatePicker dateOfTest;
    @BindView(R.id.name)
    TextInputLayout name;
    @BindView(R.id.input_number)
    TextInputLayout phoneNumberNumberInput;
    @BindView(R.id.dob)
    DatePicker dateOfBirth;
    @BindView(R.id.genderSpinner)
    Spinner genderSpinner;
    @BindView(R.id.wardNo)
    TextInputLayout wardNo;
    @BindView(R.id.landmark)
    TextInputLayout landmark;
    @BindView(R.id.houseno)
    TextInputLayout houseNo;
    @BindView(R.id.roadno)
    TextInputLayout rodeNo;
    @BindView(R.id.stateSpinner)
    Spinner stateSpinner;
    @BindView(R.id.districtSpinner)
    Spinner districtSpinner;
    @BindView(R.id.blockSpinner)
    Spinner blockSpinner;
    @BindView(R.id.progressLoader)
    TashieLoader progressLoader;
    @BindView(R.id.caretaker_name)
    TextInputLayout careTakerName;
    @BindView(R.id.caretakerNumber)
    TextInputLayout caretakerPhoneNumber;
    @BindView(R.id.consultingDoctor)
    TextInputLayout consultingDoctor;
    @BindView(R.id.adhar_fnt_btn)
    MaterialButton aadhaarFrontButton;
    @BindView(R.id.adhar_back_btn)
    MaterialButton aadhaarBackButton;
    @BindView(R.id.noOfMembers)
    TextInputLayout noOfMembers;
    @BindView(R.id.noOfSeniors)
    TextInputLayout noOfSeniors;
    @BindView(R.id.noOfChildren)
    TextInputLayout noOfChildren;
    @BindView(R.id.noOfRooms)
    TextInputLayout noOfRooms;
    @BindView(R.id.noOfWashRooms)
    TextInputLayout noOfWashRooms;
    @BindView(R.id.check_hasAttached)
    CheckBox check_hasAttached;
   @BindView(R.id.check_wasHospitalised)
   CheckBox check_wasHospitalised;
   @BindView(R.id.radio_testresult)
    RadioGroup radio_test;
    @BindView(R.id.hy_tn)
    CheckBox check_Hypertension;
    @BindView(R.id.Diabetes)
    CheckBox check_Diabetes;
    @BindView(R.id.ht_ds)
    CheckBox check_HeartDisease;
    @BindView(R.id.clud)
    CheckBox check_chronicLung;
    @BindView(R.id.clid)
    CheckBox check_chronicLiver;
    @BindView(R.id.ckd)
    CheckBox check_chronicKidney;
    @BindView(R.id.cvd)
    CheckBox check_cerebroVascular;
    @BindView(R.id.dis_co)
    CheckBox check_DiseaseCol;
    @BindView(R.id.washroom_photos_btn)
    MaterialButton uploadWashroomPhotos;
    @BindView(R.id.btnLogin)
    MaterialButton btnLogin;
    @BindView(R.id.home_photos_btn)
    MaterialButton uploadHomePhotos;
    @BindView(R.id.progressSearchingGPS)
    ProgressBar progressSearchingGPS;
    @BindView(R.id.buttonPicLocation)
    MaterialButton buttonPicLocation;
    @BindView(R.id.check_condition)
    CheckBox check_hasCondition;
    @BindView(R.id.request_btn)
    MaterialButton requestIsolationButton;
    @BindView(R.id.adhar_fnt_img)
    CircularImageView aadhaarFrontImage;
    @BindView(R.id.adhar_back_image)
    CircularImageView aadhaarBackImage;
    @BindView(R.id.imageWashroom1)
    CircularImageView imageWashroom1;
    @BindView(R.id.imageWashroom2)
    CircularImageView imageWashroom2;
    @BindView(R.id.imageHome1)
    CircularImageView imageHome1;
    @BindView(R.id.imageHome2)
    CircularImageView imageHome2;
    @BindView(R.id.imageHome3)
    CircularImageView imageHome3;
    @BindView(R.id.imageHome4)
    CircularImageView imageHome4;
    @BindView(R.id.imageHome5)
    CircularImageView imageHome5;
    @BindView(R.id.input_Long)
    TextView textLong;
    @BindView(R.id.input_Lat)
    TextView textLat;
    @BindView(R.id.term_conditions)
    TextView term_conditions;
    TextView progressDialogueTitle;
    PullInLoader progressDialogueLoader;
    MaterialButton progressDialogueDismissButton;

    String currentPhotoPath;
    Uri tempPhotoUri;
    Uri uriAadhaarFront, uriAadhaarBack;
    ArrayList< Uri > uriWashroom = new ArrayList<>();
    ArrayList< Uri > uriHome = new ArrayList<>();
    RetrofitNetworkApi retrofitNetworkApi;
    RetrofitClient retrofitClient;
    View parentLayout;
    Gson gson;
    Unbinder unbinder;
    AlertDialog alertDialogProgress;
    private int REQUEST_CODE_FOR_IMAGE = -1;
    private int washRoomPhotoCounter = 0, homePhotoCounter = 0;
    ArrayList< StateModel > stateList = new ArrayList<>();
    ArrayList< DistrictModel > districtList = new ArrayList<>();
    ArrayList< BlockModel > blockList = new ArrayList<>();
    private String blockId = null;
    private Location currLocation = null;
    Presenter presenter;
    boolean isEditing = false;
    User user;
    int testResult=1;

    @Override
    protected void onDestroy() {
        if (unbinder != null) unbinder.unbind();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        unbinder = ButterKnife.bind(this);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);
        retrofitNetworkApi = retrofitClient.getAPIService(this);
        presenter = new Presenter(this);
        parentLayout = findViewById(android.R.id.content);

        gson = new Gson();

        setOnClickListener();
        for (int i = 0; i < 2; i++)
            uriWashroom.add(null);


        for (int i = 0; i < 5; i++)
            uriHome.add(null);

        initUi();

        getStates();

        user = (User) getIntent().getSerializableExtra(HelperClass.USER);

        if(user != null){
            Log.i("EDITING", "YES");
            isEditing = true;
            updateDetails();
        }
        else{
            Log.i("EDITING", "NO");
            isEditing = false;
        }

        dateOfTest.setMaxDate(Calendar.getInstance().getTimeInMillis());
        dateOfBirth.setMaxDate(Calendar.getInstance().getTimeInMillis());

        RadioTestResult();

        SelectedGender();
    }

    private void SelectedGender() {
        String[] type = { "Not selected","Male", "Female","Prefer Not To Say"};

        ArrayAdapter< String > dataAdapter = new ArrayAdapter< String >(SignUpActivity.this,
                android.R.layout.simple_spinner_item, type);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(dataAdapter);
    }

    private void RadioTestResult() {
        radio_test.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id=radio_test.getCheckedRadioButtonId();
                View radioButton = radio_test.findViewById(id);
                if(radioButton.getId()==R.id.radio_negative)
                {
                    testResult=0;
                }
            }
        });
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

    private void initUi() {

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView< ? > adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemPosition() != -1) {
                    districtSpinner.setVisibility(View.GONE);
                    blockSpinner.setVisibility(View.GONE);

                    for (StateModel state : stateList)
                        if (state.getStateName().equals(adapterView.getSelectedItem())) {
                            progressLoader.setVisibility(View.VISIBLE);
                            getDistricts(state);
                            break;
                        }
                }
            }

            @Override
            public void onNothingSelected(AdapterView< ? > adapterView) {

            }
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView< ? > adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemPosition() != -1) {
                    blockSpinner.setVisibility(View.GONE);

                    for (DistrictModel district : districtList)
                        if (district.getDistrictName().equals(adapterView.getSelectedItem())) {
                            progressLoader.setVisibility(View.VISIBLE);
                            getBlocks(district);
                            break;
                        }
                }
            }

            @Override
            public void onNothingSelected(AdapterView< ? > adapterView) {

            }
        });

        blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView< ? > adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemPosition() == 0) {
                    HelperClass.toast(SignUpActivity.this, "Please Select Block to Continue");
                    blockId = "";
                    return;
                }

                if (adapterView.getSelectedItemPosition() != -1) {
                    for (BlockModel block : blockList)
                        if (block.getBlockName().equals(adapterView.getSelectedItem())) {
                            blockId = block.getBlockId();
                            break;
                        }
                }
            }

            @Override
            public void onNothingSelected(AdapterView< ? > adapterView) {

            }
        });
    }

    private void getBlocks(DistrictModel districtModel) {
        retrofitNetworkApi.getBlocks(districtModel).enqueue(new Callback< BlockResponse >() {
            @Override
            public void onResponse(Call< BlockResponse > call, Response< BlockResponse > response) {
                progressLoader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    blockSpinner.setVisibility(View.VISIBLE);
                    blockList = response.body().getBlocksList();

                    ArrayList< String > list = new ArrayList<>();
                    list.add("Select Block");
                    for (BlockModel blockModel : blockList) list.add(blockModel.getBlockName());

                    ArrayAdapter< String > dataAdapter = new ArrayAdapter< String >(SignUpActivity.this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    blockSpinner.setAdapter(dataAdapter);
                } else if (response.body() != null) {
                    HelperClass.toast(SignUpActivity.this, "Failed to Load Blocks \n" + response.body().getMessage());
                } else
                    HelperClass.toast(SignUpActivity.this, "Failed to Load Blocks \n");
            }

            @Override
            public void onFailure(Call< BlockResponse > call, Throwable t) {
                progressLoader.setVisibility(View.GONE);
            }
        });
    }

    private void getDistricts(StateModel requestModel) {
        retrofitNetworkApi.getDistricts(requestModel).enqueue(new Callback< DistrictResponse >() {
            @Override
            public void onResponse(Call< DistrictResponse > call, Response< DistrictResponse > response) {
                progressLoader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    districtSpinner.setVisibility(View.VISIBLE);
                    districtList = response.body().getDistrictsList();

                    ArrayList< String > list = new ArrayList<>();
                    for (DistrictModel districtModel : districtList)
                        list.add(districtModel.getDistrictName());

                    ArrayAdapter< String > dataAdapter = new ArrayAdapter< String >(SignUpActivity.this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    districtSpinner.setAdapter(dataAdapter);
                } else if (response.body() != null) {
                    HelperClass.toast(SignUpActivity.this, "Failed to Load Districts \n" + response.body().getMessage());
                } else
                    HelperClass.toast(SignUpActivity.this, "Failed to Load Districts \n");
            }

            @Override
            public void onFailure(Call< DistrictResponse > call, Throwable t) {
                progressLoader.setVisibility(View.GONE);
            }
        });
    }

    private void getStates() {
        retrofitNetworkApi.getStates().enqueue(new Callback< StateResponse >() {
            @Override
            public void onResponse(Call< StateResponse > call, Response< StateResponse > response) {
                progressLoader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    stateSpinner.setVisibility(View.VISIBLE);
                    stateList.clear();
                    stateList = response.body().getStatesList();

                    ArrayList< String > list = new ArrayList<>();
                    for (StateModel stateModel : stateList) list.add(stateModel.getStateName());

                    ArrayAdapter< String > dataAdapter = new ArrayAdapter< String >(SignUpActivity.this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    stateSpinner.setAdapter(dataAdapter);
                } else if (response.body() != null) {
                    HelperClass.toast(SignUpActivity.this, "Failed to Load States \n" + response.body().getMessage());
                } else
                    HelperClass.toast(SignUpActivity.this, "Failed to Load States \n");
            }

            @Override
            public void onFailure(Call< StateResponse > call, Throwable t) {
                progressLoader.setVisibility(View.GONE);
            }
        });
    }

    private void setOnClickListener() {
        requestIsolationButton.setOnClickListener(this);
        aadhaarFrontButton.setOnClickListener(this);
        aadhaarBackButton.setOnClickListener(this);
        uploadHomePhotos.setOnClickListener(this);
        uploadWashroomPhotos.setOnClickListener(this);
        buttonPicLocation.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        term_conditions.setOnClickListener(this);
    }

    private void takePhoto(int requestCode) {
        REQUEST_CODE_FOR_IMAGE = requestCode;
        if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignUpActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignUpActivity.this,
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
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            tempPhotoUri = FileProvider.getUriForFile(this, "com.isolate.egovdhn.in.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri);
            startActivityForResult(intent, requestCode);
        }
        //startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
        //if (resultCode == RESULT_OK && data != null) {
        if (resultCode == RESULT_OK) {
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Uri imageUri = HelperClass.getImageUri(SignUpActivity.this, imageBitmap);*/
            File f = new File(currentPhotoPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);

            Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            Uri imageUri = HelperClass.getImageUri(SignUpActivity.this, previewBitmap);

            Bitmap serverBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 768, true);
            Uri serverImageUri = HelperClass.getImageUri(SignUpActivity.this, serverBitmap);

            if (f.exists()) {
                if (f.delete()) {
                    Log.d("SignUp", "onActivityResult: File gone!");
                } else {
                    Log.d("SignUp", "onActivityResult: File not gone!");
                }
            }

            if (requestCode == GET_AADHAAR) {
                //uriAddress = imageUri;
                uriAadhaarFront = serverImageUri;
                Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_circle_24).into(aadhaarFrontImage);
                // File originalFile = FileUtils.getFile(this, uriAddress); // File if needed
            } else if (requestCode == GET_AADHAAR_BACK) {

                uriAadhaarBack = serverImageUri;
                Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_circle_24).into(aadhaarBackImage);
                // File originalFile = FileUtils.getFile(this, uriAddress); // File if needed
            } else if (requestCode <= GET_WASHROOM[1]) {
                for (int i = 0; i < 2; i++) {
                    if (requestCode == GET_WASHROOM[i]) {
                        //uriWashroom.set(i, imageUri);
                        uriWashroom.set(i, serverImageUri);
                        washRoomPhotoCounter++;
                        if (washRoomPhotoCounter >= 2) washRoomPhotoCounter = 0;
                        if (i == 0)
                            Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_circle_24).into(imageWashroom1);
                        else
                            Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_circle_24).into(imageWashroom2);
                        //File originalFile = FileUtils.getFile(this, uriWashroom.get(i));
                    }
                }
            } else {
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
                        else if (i == 3)
                            Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_circle_24).into(imageHome4);
                        else
                            Picasso.get().load(imageUri).placeholder(R.drawable.ic_baseline_circle_24).into(imageHome5);
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
                if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignUpActivity.this,
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

    @Override
    protected void onPause() {
        retrofitClient.removeInternetConnectionListener();
        super.onPause();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.request_btn:
                if (isValid())
                    getFCMToken();
                break;
            case R.id.adhar_fnt_btn:
                takePhoto(GET_AADHAAR);
                break;
            case R.id.adhar_back_btn:
                takePhoto(GET_AADHAAR_BACK);
                break;
            case R.id.washroom_photos_btn:
                takePhoto(GET_WASHROOM[washRoomPhotoCounter]);
                break;
            case R.id.home_photos_btn:
                takePhoto(GET_HOME[homePhotoCounter]);
                break;
            case R.id.buttonPicLocation:
                searchForGPS();
                break;
            case R.id.btnLogin:
                Intent intent = new Intent(this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.term_conditions:
                String url = "isolate.egovdhn.in/terms";
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;

                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                break;
        }
    }

    private boolean isValid() {
        String srfId = srfID.getEditText().getText().toString().trim();
        String name_user = name.getEditText().getText().toString().trim();
        String phoneNumber = phoneNumberNumberInput.getEditText().getText().toString().trim();
        String wardNo = this.wardNo.getEditText().getText().toString().trim();
        String landmark = this.landmark.getEditText().getText().toString().trim();
        String house_no = houseNo.getEditText().getText().toString().trim();
        String road_no = rodeNo.getEditText().getText().toString().trim();
        String caretaker_name = careTakerName.getEditText().getText().toString().trim();
        String caretaker_phone = caretakerPhoneNumber.getEditText().getText().toString().trim();
        String consulting_doctor = consultingDoctor.getEditText().getText().toString().trim();
        String no_of_rooms = noOfRooms.getEditText().getText().toString().trim();
        String no_of_washrooms = noOfWashRooms.getEditText().getText().toString().trim();
        String no_of_members = noOfMembers.getEditText().getText().toString().trim();
        String no_of_seniors = noOfSeniors.getEditText().getText().toString().trim();
        String no_of_children = noOfChildren.getEditText().getText().toString().trim();
        TextView errorText = (TextView)genderSpinner.getSelectedView();

        boolean isConditionsAccepted = check_hasCondition.isChecked();

        if (srfId.length() < 13) {
            srfID.setError("Please Provide a valid SRF ID");
            srfID.requestFocus();
            return false;
        } else srfID.setError(null);

        if (name_user.isEmpty()) {
            name.setError("Name is Required");
            name.requestFocus();
            return false;
        } else name.setError(null);

        if (phoneNumber.length() != 10) {
            phoneNumberNumberInput.setError("Please Provide a valid Phone Number");
            phoneNumberNumberInput.requestFocus();
            return false;
        } else phoneNumberNumberInput.setError(null);

        if(genderSpinner.getSelectedItemPosition()==0)
        { genderSpinner.setFocusable(true);
        genderSpinner.setFocusableInTouchMode(true);
        errorText.setError("");
        genderSpinner.requestFocus();
        errorText.setTextColor(Color.RED);
        errorText.setText("Please provide your gender");
        return false;
        }
        else
            errorText.setError(null);

        if (house_no.isEmpty()) {
            houseNo.setError("House Number or Building Name is Required");
            houseNo.requestFocus();
            return false;
        } else houseNo.setError(null);

        if (road_no.isEmpty()) {
            rodeNo.setError("Road Number or Colony Name is Required");
            rodeNo.requestFocus();
            return false;
        } else rodeNo.setError(null);

        if (wardNo.isEmpty()) {
            this.wardNo.setError("Ward Number or Panchayat Name is Required");
            this.wardNo.requestFocus();
            return false;
        } else this.wardNo.setError(null);

        if (landmark.isEmpty()) {
            this.landmark.setError("Landmark or P.S. is Required");
            this.landmark.requestFocus();
            return false;
        } else this.landmark.setError(null);

        if (caretaker_name.isEmpty()) {
            careTakerName.setError("Care Taker Name is Required");
            careTakerName.requestFocus();
            return false;
        } else careTakerName.setError(null);

        if (caretaker_phone.length() != 10) {
            caretakerPhoneNumber.setError("Please Provide a valid Phone Number");
            caretakerPhoneNumber.requestFocus();
            return false;
        } else caretakerPhoneNumber.setError(null);

        if (consulting_doctor.isEmpty()) {
            consultingDoctor.setError("Name of consulting doctor is Required");
            consultingDoctor.requestFocus();
            return false;
        } else consultingDoctor.setError(null);

        if (no_of_rooms.isEmpty()) {
            noOfRooms.setError("This is a mandatory Field");
            noOfRooms.requestFocus();
            return false;
        } else noOfRooms.setError(null);

        if (no_of_washrooms.isEmpty()) {
            noOfWashRooms.setError("This is a mandatory Field");
            noOfWashRooms.requestFocus();
            return false;
        } else noOfWashRooms.setError(null);

        if (no_of_members.isEmpty()) {
            noOfMembers.setError("This is a mandatory Field");
            noOfMembers.requestFocus();
            return false;
        } else noOfMembers.setError(null);

        if (no_of_seniors.isEmpty()) {
            noOfSeniors.setError("This is a mandatory Field");
            noOfSeniors.requestFocus();
            return false;
        } else noOfSeniors.setError(null);

        if (no_of_children.isEmpty()) {
            noOfChildren.setError("This is a mandatory Field");
            noOfChildren.requestFocus();
            return false;
        } else noOfChildren.setError(null);

        if (uriAadhaarFront == null) {
            HelperClass.toast(this, "Please submit Aadhaar Front Photo");
            return false;
        }

        if (uriAadhaarBack == null) {
            HelperClass.toast(this, "Please submit Aadhaar Back Photo");
            return false;
        }

        boolean washroomPhotosUploaded = true;
        for (Uri uri : uriWashroom) if (uri == null) washroomPhotosUploaded = false;
        if (!washroomPhotosUploaded) {
            HelperClass.toast(this, "Please submit 2 Photos of your Washrooms");
            return false;
        }

        boolean homePhotosUploaded = true;
        for (Uri uri : uriHome) if (uri == null) homePhotosUploaded = false;
        if (!homePhotosUploaded) {
            HelperClass.toast(this, "Please submit 5 Photos of your Home");
            return false;
        }

        if (blockSpinner.getSelectedItemPosition() == -1 || blockSpinner.getSelectedItemPosition() == 0 &&
                blockId.isEmpty()) {
            HelperClass.toast(this, "Please Select State, District & Block");
            return false;
        }

        if (currLocation == null) {
            HelperClass.toast(this, "Please Provide your location by clicking PicLocation Button");
            return false;
        }

        if (!isConditionsAccepted) {
            HelperClass.toast(this, "Please accept the Terms & Conditions to Continue");
            return false;
        }

        try {
            String dob = presenter.datify(dateOfBirth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateDob = sdf.parse(dob);
            long time_difference = (new Date()).getTime() - dateDob.getTime();
            long years_difference = TimeUnit.MILLISECONDS.toDays(time_difference) / 365L;
            //Removing 45 years issue
            /*if (years_difference > 45) {
                HelperClass.toast(this, "You must be equal to or below 45 years of age at the time of applying.");
                return false;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void sendIsolationRequest(String fcm_token) {
        String srfId = srfID.getEditText().getText().toString().trim();
        String name_user = name.getEditText().getText().toString().trim();
        String phoneNumber = phoneNumberNumberInput.getEditText().getText().toString().trim();
        String wardNo = this.wardNo.getEditText().getText().toString().trim();
        String landmark = this.landmark.getEditText().getText().toString().trim();
        String house_no = houseNo.getEditText().getText().toString().trim();
        String road_no = rodeNo.getEditText().getText().toString().trim();
        String caretaker_name = careTakerName.getEditText().getText().toString().trim();
        String caretaker_phone = caretakerPhoneNumber.getEditText().getText().toString().trim();
        String consulting_doctor = consultingDoctor.getEditText().getText().toString().trim();
        String no_of_rooms = noOfRooms.getEditText().getText().toString().trim();
        String no_of_washrooms = noOfWashRooms.getEditText().getText().toString().trim();
        String no_of_members = noOfMembers.getEditText().getText().toString().trim();
        String no_of_seniors = noOfSeniors.getEditText().getText().toString().trim();
        String no_of_children = noOfChildren.getEditText().getText().toString().trim();
        boolean check_has_attached = check_hasAttached.isChecked();
        boolean wasHospitalised = check_wasHospitalised.isChecked();
        int test_result= testResult;
        String diseases = getConcatenatedDiseases();
        String gender = String.valueOf(genderSpinner.getSelectedItem());

        String address = house_no + ", " + road_no + ", " + wardNo + ", " + landmark + ", " + districtSpinner.getSelectedItem() + ", " + stateSpinner.getSelectedItem();
        Map< String, Object > mp = new HashMap<>();
        mp.put("srf_id", srfId);
        mp.put("name", name_user);
        mp.put("phone", phoneNumber);
        mp.put("device_id", fcm_token);
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        //sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        String birthDate = presenter.datify(dateOfBirth);
        String testDate = presenter.datify(dateOfTest);
        Log.d("Dating", "sendIsolationRequest: " + birthDate + " " + testDate);
        mp.put("dob", birthDate);
        mp.put("gender",gender);
        mp.put("test_date", testDate);
        mp.put("block_id", blockId);
        mp.put("address", address);
        mp.put("caretaker_name", caretaker_name);
        mp.put("caretaker_phone", caretaker_phone);
        mp.put("doctor", consulting_doctor);
        mp.put("rooms", no_of_rooms);
        mp.put("washrooms", no_of_washrooms);
        mp.put("family_total", no_of_members);
        mp.put("family_above_60", no_of_seniors);
        mp.put("family_below_10", no_of_children);
        mp.put("hasAttached", check_has_attached ? 1 : 0);
        mp.put("latitude", currLocation.getLatitude());
        mp.put("longitude", currLocation.getLongitude());
        mp.put("wasHospitalised", wasHospitalised ? 1 : 0);
        mp.put("diseases", diseases);
        mp.put("test_result", test_result);

        String fStatus = gson.toJson(mp);
        RequestBody infoPart = RequestBody.create(MultipartBody.FORM, fStatus);

        MultipartBody.Part fileAadhaarFront, fileAadhaarBack;
        ArrayList< MultipartBody.Part > fileWashroom = new ArrayList<>(), fileHome = new ArrayList<>();

        File originalFile;
        RequestBody filePart;

        originalFile = FileUtils.getFile(this, uriAadhaarFront);
        filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(uriAadhaarFront)), originalFile);
        fileAadhaarFront = MultipartBody.Part.createFormData("aadhaarFront", originalFile.getName(), filePart);

        originalFile = FileUtils.getFile(this, uriAadhaarBack);
        filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(uriAadhaarBack)), originalFile);
        fileAadhaarBack = MultipartBody.Part.createFormData("aadhaarBack", originalFile.getName(), filePart);

        for (int i = 0; i < 2; i++) {
            originalFile = FileUtils.getFile(this, uriWashroom.get(i));
            filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(uriWashroom.get(i))), originalFile);
            fileWashroom.add(MultipartBody.Part.createFormData("washroom" + (i + 1), originalFile.getName(), filePart));
        }

        for (int i = 0; i < 5; i++) {
            originalFile = FileUtils.getFile(this, uriHome.get(i));
            filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(uriHome.get(i))), originalFile);
            fileHome.add(MultipartBody.Part.createFormData("home" + (i + 1), originalFile.getName(), filePart));
        }

        showProgressDialogue();

        if (!isEditing) {
            presenter.signUp(retrofitNetworkApi, infoPart, fileAadhaarFront, fileAadhaarBack, fileWashroom.get(0), fileWashroom.get(1),
                    fileHome.get(0), fileHome.get(1), fileHome.get(2), fileHome.get(3), fileHome.get(4));
        } else {
            presenter.updateForm(retrofitNetworkApi, infoPart, fileAadhaarFront, fileAadhaarBack, fileWashroom.get(0), fileWashroom.get(1),
                    fileHome.get(0), fileHome.get(1), fileHome.get(2), fileHome.get(3), fileHome.get(4));
        }

    }

    private String getConcatenatedDiseases() {
        String res = "";
        if (check_Hypertension.isChecked())
            res = res + "Hypertension, ";
        if (check_Diabetes.isChecked())
            res = res + "Diabetes, ";
        if (check_HeartDisease.isChecked())
            res = res + "Heart Disease, ";
        if (check_chronicLung.isChecked())
            res = res + "Chronic Lung Disease, ";
        if (check_chronicLiver.isChecked())
            res = res + "Chronic Liver Disease, ";
        if (check_chronicKidney.isChecked())
            res = res + "Chronic Kidney Disease, ";
        if (check_cerebroVascular.isChecked())
            res = res + "Cerebro Vascular Disease, ";
        if (check_DiseaseCol.isChecked())
            res = res + "Disease Col, ";
        if (res.isEmpty())
            res = "None";
        return res;
    }

    private void showProgressDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_loading, null);
        builder.setView(view);
        progressDialogueTitle = view.findViewById(R.id.textViewTitle);
        progressDialogueDismissButton = view.findViewById(R.id.dismissButton);
        progressDialogueLoader = view.findViewById(R.id.progressLoader);

        progressDialogueTitle.setText("Please wait, we are creating an Isolation Request...");

        alertDialogProgress = builder.create();
        alertDialogProgress.setCancelable(false);
        alertDialogProgress.show();

        alertDialogProgress.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialogProgress.getWindow().setBackgroundDrawable(null);
        alertDialogProgress.getWindow().setGravity(Gravity.BOTTOM);

        progressDialogueDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogProgress.dismiss();
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currLocation = location;
    }

    @Override
    public void setResultsUI(String message) {
        progressDialogueTitle.setText(message);
        progressDialogueLoader.setVisibility(View.GONE);
        progressDialogueDismissButton.setVisibility(View.VISIBLE);
    }

    private void updateDetails() {
        if(user == null) return;

        srfID.getEditText().setText(user.getSrfId());
        srfID.setEnabled(false);
        name.getEditText().setText(user.getName());
        phoneNumberNumberInput.getEditText().setText(user.getPhone());
        phoneNumberNumberInput.setEnabled(false);
//        this.landmark.getEditText().setText(user.getAddress());
//        houseNo.getEditText().setText("");
//        rodeNo.getEditText().setText("");
        careTakerName.getEditText().setText(user.getCaretakerName());
        caretakerPhoneNumber.getEditText().setText(user.getCaretakerPhone());
        consultingDoctor.getEditText().setText(user.getDoctor());
        noOfRooms.getEditText().setText(user.getRooms());
        noOfWashRooms.getEditText().setText(user.getWashrooms());
        noOfMembers.getEditText().setText(user.getFamilyTotal());
        noOfSeniors.getEditText().setText(user.getFamilyAbove60());
        noOfChildren.getEditText().setText(user.getFamilyBelow10());
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
                        sendIsolationRequest(token);
                    }
                });

    }

    @Override
    public void onInternetUnavailable() {
        Snackbar.make(parentLayout, "No Internet Connection Available", Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onCacheUnavailable() {
    }
}
