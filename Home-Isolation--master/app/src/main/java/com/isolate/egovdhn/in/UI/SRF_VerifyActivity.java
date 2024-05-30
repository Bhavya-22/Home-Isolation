package com.isolate.egovdhn.in.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.FormDataResponse;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.InternetConnectionListener;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.UI.SignUp.SignUpActivity;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SRF_VerifyActivity extends AppCompatActivity implements InternetConnectionListener {
    @BindView(R.id.textInputId)
    TextInputLayout textInputId;
    @BindView(R.id.textInputPassword)
    TextInputLayout textInputPassword;
    @BindView(R.id.btnLogin2)
    MaterialButton btnLogin;

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
        setContentView(R.layout.activity_s_r_f__verify);
        parentLayout = findViewById(android.R.id.content);
        unbinder = ButterKnife.bind(this);
        retrofitClient = ((RetrofitClient) getApplication());
        retrofitClient.setInternetConnectionListener(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = textInputId.getEditText().getText().toString().trim();
                String password = textInputPassword.getEditText().getText().toString().trim();

                if (id == null || id.length() != 13) {
                    textInputId.setError("Please Enter a valid SRF ID");
                    textInputId.requestFocus();
                    return;
                } else textInputId.setError(null);

                if (password==null || password.length()!=10 ) {
                    textInputPassword.setError("Not a valid phone number");
                    textInputPassword.requestFocus();
                    return;
                } else textInputPassword.setError(null);

                showProgressDialogue();
                getModel(id, password);
            }
        });
    }

    private void getModel(String id, String password) {
        RetrofitNetworkApi retrofitNetworkApi = retrofitClient.getAPIService(this);
        HashMap<String, String> body = new HashMap<>();
        body.put("srf_id", id);
        retrofitNetworkApi.getUser(body).enqueue(new Callback<FormDataResponse>() {
            @Override
            public void onResponse(Call<FormDataResponse> call, Response<FormDataResponse> response) {
                if (response.isSuccessful() && response.body().getStatus()==200 && response.body() != null
                        && response.body().getUser() !=null && response.body().getUser().getSrfId() != null ) {
                    alertDialogProgress.dismiss();
                    String phone = response.body().getUser().getPhone();
                    if (phone != null && phone.equals(password)) {
                        HelperClass.toast(SRF_VerifyActivity.this, "User found!");

                        Intent intent = new Intent(getApplication(), SignUpActivity.class);
                        intent.putExtra(HelperClass.USER, response.body().getUser());
                        startActivity(intent);
                        finish();
                    } else {
                        HelperClass.toast(SRF_VerifyActivity.this, "Credentials Mismatch!");
                    }
                } else if (response.body().getStatus() != 200) {
                    setResultsUI(response.body().getMessage());
                    finish();
                } else {
                    setResultsUI("Failed to Find User -\n (May be you are not connected to Internet");
                }
            }

            @Override
            public void onFailure(Call<FormDataResponse> call, Throwable t) {
                setResultsUI("Failed to Find User -\n (May be you are not connected to Internet");
            }
        });
    }

    private void showProgressDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SRF_VerifyActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_loading, null);
        builder.setView(view);
        progressDialogueTitle = view.findViewById(R.id.textViewTitle);
        progressDialogueDismissButton = view.findViewById(R.id.dismissButton);
        progressDialogueLoader = view.findViewById(R.id.progressLoader);

        progressDialogueTitle.setText("Please Wait...");

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
        HelperClass.toast(this, message);
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