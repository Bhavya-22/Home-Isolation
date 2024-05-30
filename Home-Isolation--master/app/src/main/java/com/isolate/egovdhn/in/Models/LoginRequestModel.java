package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class LoginRequestModel {
    @SerializedName("srfId")
    String srfId;
    @SerializedName("password")
    String password;
    @SerializedName("device_id")
    String device_id;

    public LoginRequestModel(String srfId, String password) {
        this.srfId = srfId;
        this.password = password;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getSrfId() {
        return srfId;
    }

    public void setSrfId(String srfId) {
        this.srfId = srfId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
