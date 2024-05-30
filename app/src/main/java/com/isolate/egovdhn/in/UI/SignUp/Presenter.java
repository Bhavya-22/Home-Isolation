package com.isolate.egovdhn.in.UI.SignUp;

import android.widget.DatePicker;

import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.ResponseUpload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Presenter implements Contract.Presenter {
    Contract.View view;

    public Presenter(Contract.View view) {
        this.view = view;
    }

    @Override
    public void signUp(RetrofitNetworkApi retrofitNetworkApi, RequestBody info, MultipartBody.Part aadhaarFront, MultipartBody.Part aadhaarBack,
                       MultipartBody.Part washroom1, MultipartBody.Part washroom2, MultipartBody.Part home1,
                       MultipartBody.Part home2, MultipartBody.Part home3, MultipartBody.Part home4,
                       MultipartBody.Part home5) {

        Call<ResponseUpload> call = retrofitNetworkApi.sendIsolationRequest(
                info, aadhaarFront, aadhaarBack, washroom1, washroom2,
                home1, home2, home3, home4, home5);

        call.enqueue(new Callback<ResponseUpload>() {
            @Override
            public void onResponse(Call<ResponseUpload> call, Response<ResponseUpload> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.setResultsUI(response.body().message);
                } else if (response.body() != null) {
                    view.setResultsUI("Failed to Register \n" + response.body().message);
                } else {
                    view.setResultsUI("Request Error \n(May be No Internet Available)");
                }
            }

            @Override
            public void onFailure(Call<ResponseUpload> call, Throwable t) {
                view.setResultsUI("Failed to Register");
            }
        });
    }

    @Override
    public void updateForm(RetrofitNetworkApi retrofitNetworkApi, RequestBody info, MultipartBody.Part aadhaarFront, MultipartBody.Part aadhaarBack,
                           MultipartBody.Part washroom1, MultipartBody.Part washroom2, MultipartBody.Part home1,
                           MultipartBody.Part home2, MultipartBody.Part home3, MultipartBody.Part home4,
                           MultipartBody.Part home5) {

        Call<ResponseUpload> call = retrofitNetworkApi.updateIsolationRequest(
                info, aadhaarFront, aadhaarBack, washroom1, washroom2,
                home1, home2, home3, home4, home5);

        call.enqueue(new Callback<ResponseUpload>() {
            @Override
            public void onResponse(Call<ResponseUpload> call, Response<ResponseUpload> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.setResultsUI(response.body().message);
                } else if (response.body() != null) {
                    view.setResultsUI("Failed to Update \n" + response.body().message);
                } else {
                    view.setResultsUI("Request Error \n(May be No Internet Available)");
                }
            }

            @Override
            public void onFailure(Call<ResponseUpload> call, Throwable t) {
                view.setResultsUI("Failed to Update");
            }
        });
    }

    @Override
    public String datify(DatePicker datePicker) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        String sYear = String.valueOf(year);
        String sMonth = "";
        if (month + 1 < 10)
            sMonth = "0";
        sMonth = sMonth + (month + 1);
        String sDay = String.valueOf(day);
        if (day < 10)
            sDay = "0" + sDay;
        //return sYear + "-" + sMonth + "-" + sDay + "T08:33:36.116Z";
        return sYear + "-" + sMonth + "-" + sDay;
    }
}
