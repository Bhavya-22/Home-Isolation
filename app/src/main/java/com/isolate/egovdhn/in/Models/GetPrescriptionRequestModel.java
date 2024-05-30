package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class GetPrescriptionRequestModel {
    @SerializedName("srf_id")
    public String srf_id;

    public GetPrescriptionRequestModel(String srf_id) {
        this.srf_id = srf_id;
    }
}
