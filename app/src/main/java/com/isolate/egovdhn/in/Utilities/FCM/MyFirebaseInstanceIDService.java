package com.isolate.egovdhn.in.Utilities.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Models.FCMTokenRequest;
import com.isolate.egovdhn.in.Models.ResponseUpload;
import com.isolate.egovdhn.in.Retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        saveTokenToServer(refreshedToken);
    }

    private void saveTokenToServer(String token) {
        FCMTokenRequest request = new FCMTokenRequest(Prefs.getUser(this).getSrfId(), token);
        RetrofitNetworkApi networkApi = ((RetrofitClient) getApplication()).getAPIService(this);
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
}
