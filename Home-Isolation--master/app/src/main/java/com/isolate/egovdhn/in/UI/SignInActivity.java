package com.isolate.egovdhn.in.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.LoginRequestModel;
import com.isolate.egovdhn.in.Models.ResponseUpload;
import com.isolate.egovdhn.in.Models.UserModel;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.UI.SignUp.SignUpActivity;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity implements InternetConnectionListener {
    private static final String TAG = "SignInActivity";
    @BindView(R.id.textInputId)
    TextInputLayout textInputId;
    @BindView(R.id.textInputPassword)
    TextInputLayout textInputPassword;
    @BindView(R.id.btnLogin)
    MaterialButton btnLogin;
    @BindView(R.id.textForgotPassword)
    TextView textForgotPassword;
    @BindView(R.id.btnRegistration)
    MaterialButton btnRegistration;
    @BindView(R.id.userManual)
    MaterialButton userManual;

    Unbinder unbinder;
    TextView progressDialogueTitle;
    PullInLoader progressDialogueLoader;
    MaterialButton progressDialogueDismissButton;
    AlertDialog alertDialogProgress;
    RetrofitClient retrofitClient;
    View parentLayout;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        parentLayout = findViewById(android.R.id.content);
        unbinder = ButterKnife.bind(this);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);

        findViewById(R.id.textForgotPassword2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SRF_VerifyActivity.class));
            }
        });
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = textInputId.getEditText().getText().toString().trim();
                String password = textInputPassword.getEditText().getText().toString().trim();

                if (id.length() != 13) {
                    textInputId.setError("Please Enter a valid SRF ID");
                    textInputId.requestFocus();
                    return;
                } else textInputId.setError(null);

                if (password.isEmpty()) {
                    textInputPassword.setError("Please Enter a Password");
                    textInputPassword.requestFocus();
                    return;
                } else textInputPassword.setError(null);

                showProgressDialogue();
                LoginRequestModel requestModel = new LoginRequestModel(id, password);
                getFCMToken(requestModel);
            }
        });

        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordDialogFragment dialogFragment = new ResetPasswordDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "ResetPasswordDialogFragment");
            }
        });

        userManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "isolate.egovdhn.in/usermanual";
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;

                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void login(LoginRequestModel requestModel) {
        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        retrofitNetworkApi.getLogin(requestModel).enqueue(new Callback<ResponseUpload>() {
            @Override
            public void onResponse(Call<ResponseUpload> call, Response<ResponseUpload> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status == 201) {
                    Log.d(TAG, "onResponse: "+response.body().message);
                    alertDialogProgress.dismiss();
                    HelperClass.toast(SignInActivity.this, "Login Success");
                    Prefs.setUserLoggedIn(SignInActivity.this, true);
                    Prefs.setToken(SignInActivity.this, response.body().token);
                    Prefs.SetUserData(SignInActivity.this, new UserModel(requestModel.getSrfId()));

                    Intent signUpIntent = new Intent(getApplication(), MainActivity.class);
                    signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    signUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(signUpIntent);
                    finish();
                } else if (response.body() != null && response.body().message != null) {
                    Log.d(TAG, "onResponse: "+response.body().message);
                    setResultsUI(response.body().message);
                } else {
                    Log.d(TAG, "onResponse: This case");
                    setResultsUI("Request Error \n(May be No Internet Available)");
                }
            }

            @Override
            public void onFailure(Call<ResponseUpload> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
            }
        });
    }

    private void showProgressDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_loading, null);
        builder.setView(view);
        progressDialogueTitle = view.findViewById(R.id.textViewTitle);
        progressDialogueDismissButton = view.findViewById(R.id.dismissButton);
        progressDialogueLoader = view.findViewById(R.id.progressLoader);

        progressDialogueTitle.setText("Please wait we are checking the details....");

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

    private void setResultsUI(String message) {
        progressDialogueTitle.setText(message);
        progressDialogueLoader.setVisibility(View.GONE);
        progressDialogueDismissButton.setVisibility(View.VISIBLE);
    }

    private void getFCMToken(LoginRequestModel requestModel) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.d("Task : ", "Hi getInstanceId failed * " + task.getException());
                    return;
                }
                String token = task.getResult().getToken();
                Log.d("FCM_TOKEN", token);
                requestModel.setDevice_id(token);
                login(requestModel);
            }
        });

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
