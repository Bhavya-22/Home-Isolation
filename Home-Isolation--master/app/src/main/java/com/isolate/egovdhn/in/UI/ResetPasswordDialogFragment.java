package com.isolate.egovdhn.in.UI;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.ResetPasswordRequestModel;
import com.isolate.egovdhn.in.Models.ResponseResetPassword;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordDialogFragment extends BottomSheetDialogFragment {

    private static final String TAG = "ResetPasswordDialogFrag";

    Button reset_btn;
    TextInputLayout srfId;
    TashieLoader progressLoader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_reset_password, container, false);

        srfId = view.findViewById(R.id.srfId);
        reset_btn = view.findViewById(R.id.reset_btn);
        progressLoader = view.findViewById(R.id.progressLoader);

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textSrfId = srfId.getEditText().getText().toString().trim();
                if (textSrfId.isEmpty()) {
                    srfId.setError("SRF Id is Required");
                    srfId.requestFocus();
                    return;
                } else{
                    srfId.setError(null);
                }

                resetPassword(textSrfId);
            }
        });
        return view;
    }

    private void resetPassword(String textSrfId) {
        progressLoader.setVisibility(View.VISIBLE);
        setCancelable(false);

        ResetPasswordRequestModel model = new ResetPasswordRequestModel(textSrfId);
        RetrofitNetworkApi retrofitNetworkApi = ((RetrofitClient) getActivity().getApplication()).getAPIService(getContext());
        retrofitNetworkApi.resetPassword(model).enqueue(new Callback<ResponseResetPassword>() {
            @Override
            public void onResponse(Call<ResponseResetPassword> call, Response<ResponseResetPassword> response) {
                setCancelable(true);
                if(response.isSuccessful() && response.body() != null){
                    HelperClass.toast(getActivity(), response.body().message);
                }else{
                    Log.d(TAG, "onResponse: " + response.errorBody().toString());
                }
                dismiss();
            }

            @Override
            public void onFailure(Call<ResponseResetPassword> call, Throwable t) {
                setCancelable(true);
                Log.d(TAG, "onFailure: Failed to send request." + t.getLocalizedMessage());
                dismiss();
            }
        });

    }

    public interface OtpBottomSheetListener{
        void onSubmitClicked(String otp);
        void onResendClicked();
    }
}
