package com.isolate.egovdhn.in.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.work.WorkManager;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelperClass {
    public static final int LOCATION_SERVICE_ID = 5013;
    public static final String ACTION_START_LOCATION_SERVICE = "ACTION_START_LOCATION_SERVICE";
    public static final String ACTION_STOP_LOCATION_SERVICE = "ACTION_STOP_LOCATION_SERVICE";
    public static String MY_PREFS_NAME = "HIMMAT_SHARED_PREFS";
    public static String BASE_URL = "http://isolate.egovdhn.in/";
    public static String USER = "user";

    public static void toast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public static void showProgressbar(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    public static void hideProgressbar(ProgressBar progressBar) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    public static Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    public static SimpleDateFormat serverDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static String dateFormatter(Date date){
        SimpleDateFormat dest = new SimpleDateFormat("dd MMMM yyyy");
        return dest.format(date);
    }

    public static String timeFormatter(Date date){
        SimpleDateFormat dest = new SimpleDateFormat("hh:mm a");
        return dest.format(date);
    }

    public static void cancelAllNotifications(Context context){
        WorkManager.getInstance(context).cancelAllWorkByTag(NotifyWorker.TAG);
    }

    public static SimpleDateFormat serverDateFormat2(){
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public static String dateFormatter2(Date date){
        SimpleDateFormat dest = new SimpleDateFormat("dd MMMM yyyy");
        return dest.format(date);
    }
}
