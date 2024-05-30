package com.isolate.egovdhn.in.Retrofit;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbb20.BuildConfig;
import com.isolate.egovdhn.in.Database.Prefs;
import com.isolate.egovdhn.in.Database.RetrofitNetworkApi;
import com.isolate.egovdhn.in.Utilities.HelperClass;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient extends Application {
    public static final int DISK_CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    private InternetConnectionListener mInternetConnectionListener;

    private static final Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private static Retrofit retrofit = null;

    @Override
    public void onCreate() {
        // MOST IMPORTANT to get Application Context.
        super.onCreate();
    }

    public RetrofitNetworkApi getAPIService(Context context) {
        return getClient(HelperClass.BASE_URL,
                Prefs.getToken(context)).create(RetrofitNetworkApi.class);
    }

    public void setInternetConnectionListener(InternetConnectionListener listener) {
        mInternetConnectionListener = listener;
    }

    public void removeInternetConnectionListener() {
        mInternetConnectionListener = null;
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Retrofit getClient(String baseUrl, String authToken) {

        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.cache(getCache());

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                logging.redactHeader("Authorization");
                logging.redactHeader("Cookie");

                httpClient.addInterceptor(logging);
            }

            httpClient.addInterceptor(new NetworkConnectionInterceptor() {
                @Override
                public boolean isInternetAvailable() {
                    return RetrofitClient.this.isInternetAvailable();
                }

                @Override
                public void onInternetUnavailable() {
                    if (mInternetConnectionListener != null) {
                        mInternetConnectionListener.onInternetUnavailable();
                    }
                }

                @Override
                public void onCacheUnavailable() {
                    if (mInternetConnectionListener != null) {
                        mInternetConnectionListener.onCacheUnavailable();
                    }
                }
            });

            Retrofit.Builder builder =
                    new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create(gson));

            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                builder.client(httpClient.build());
                return retrofit = builder.build();
            }
        }
        return retrofit;
    }

    public Cache getCache() {
        File cacheDir = new File(getCacheDir(), "cache");
        return new Cache(cacheDir, DISK_CACHE_SIZE);
    }
}
