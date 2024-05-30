package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DistrictResponse {
    @SerializedName("status")
    int status;

    @SerializedName("message")
    String message;

    @SerializedName("districts")
    ArrayList<DistrictModel> districtsList;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<DistrictModel> getDistrictsList() {
        return districtsList;
    }
}
