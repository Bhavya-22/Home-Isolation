package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class AddAlertRequestModel {
    @SerializedName("srf_id")
    public String srf_id;

    @SerializedName("description")
    public String description;

    public AddAlertRequestModel(String srf_id, String description) {
        this.srf_id = srf_id;
        this.description = description;
    }
}
