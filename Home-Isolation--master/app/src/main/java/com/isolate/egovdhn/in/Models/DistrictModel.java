package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class DistrictModel {
    @SerializedName("sId")
    String stateId;

    @SerializedName("dId")
    String districtId;

    @SerializedName("dName")
    String districtName;

    public DistrictModel(String stateId, String districtId) {
        this.stateId = stateId;
        this.districtId = districtId;
    }

    public String getStateId() {
        return stateId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public String getDistrictName() {
        return districtName;
    }
}
