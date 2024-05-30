package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class LocationRequestModel {
    @SerializedName("latitude")
    double latitude;

    @SerializedName("longitude")
    double longitude;

    @SerializedName("srf_id")
    String srf_id;

    public LocationRequestModel(double latitude, double longitude, String srf_id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.srf_id = srf_id;
    }
}
