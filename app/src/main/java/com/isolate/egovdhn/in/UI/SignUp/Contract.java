package com.isolate.egovdhn.in.UI.SignUp;

import android.widget.DatePicker;

import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Contract {
    interface View {
        void setResultsUI(String message);
    }

    interface Presenter {

        String datify(DatePicker datePicker);

        void signUp(RetrofitNetworkApi retrofitNetworkApi, RequestBody info, MultipartBody.Part aadhaarFront, MultipartBody.Part aadhaarBack,
                    MultipartBody.Part washroom1, MultipartBody.Part washroom2, MultipartBody.Part home1,
                    MultipartBody.Part home2, MultipartBody.Part home3, MultipartBody.Part home4,
                    MultipartBody.Part home5);

        void updateForm(RetrofitNetworkApi retrofitNetworkApi, RequestBody info, MultipartBody.Part aadhaarFront, MultipartBody.Part aadhaarBack,
                        MultipartBody.Part washroom1, MultipartBody.Part washroom2, MultipartBody.Part home1,
                        MultipartBody.Part home2, MultipartBody.Part home3, MultipartBody.Part home4,
                        MultipartBody.Part home5);
    }
}
