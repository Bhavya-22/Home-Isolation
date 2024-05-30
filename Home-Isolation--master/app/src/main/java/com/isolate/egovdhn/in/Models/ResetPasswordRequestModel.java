package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequestModel {
    @SerializedName("srf_id")
    public String srf_id;

    public ResetPasswordRequestModel(String srf_id) {
        this.srf_id = srf_id;
    }
}
