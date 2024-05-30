package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class GetRecordsRequestModel {
    @SerializedName("srf_id")
    public String srf_id;

    public GetRecordsRequestModel(String srf_id) {
        this.srf_id = srf_id;
    }
}
