package com.isolate.egovdhn.in.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.ChangePasswordRequestModel;
import com.isolate.egovdhn.in.Models.ResponseChangePassword;
import com.isolate.egovdhn.in.R;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordDialogFragment extends DialogFragment {
    private static final String TAG = "ChangePasswordDialogFra";

    private TextInputLayout old_password, new_password;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.change_password_dialog, null);
        old_password = view.findViewById(R.id.old_password);
        new_password = view.findViewById(R.id.new_password);

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

                        setCancelable(false);
                        ChangePasswordRequestModel model = new ChangePasswordRequestModel(Prefs.getUser(getContext()).getSrfId(), oldPassword, newPassword);
                        RetrofitNetworkApi retrofitNetworkApi = ((RetrofitClient) getActivity().getApplication()).getAPIService(getContext());
                        retrofitNetworkApi.changePassword(model).enqueue(new Callback<ResponseChangePassword>() {
                            @Override
                            public void onResponse(Call<ResponseChangePassword> call, Response<ResponseChangePassword> response) {
                                setCancelable(true);
                                if(response.isSuccessful() && response.body() != null){
                                    HelperClass.toast(getActivity(), response.body().message);
                                }else{
                                    Log.d(TAG, "onResponse: " + response.errorBody().toString());
                                }
                                dismiss();
                            }

                            @Override
                            public void onFailure(Call<ResponseChangePassword> call, Throwable t) {
                                setCancelable(true);
                                Log.d(TAG, "onFailure: Failed to send request." + t.getLocalizedMessage());
                                dismiss();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
