package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class FCMTokenRequest {
    @SerializedName("srf_id")
    String srfId;

    @SerializedName("device_id")
    String device_id;

    public FCMTokenRequest(String srfId, String device_id) {
        this.srfId = srfId;
        this.device_id = device_id;
    }
}
