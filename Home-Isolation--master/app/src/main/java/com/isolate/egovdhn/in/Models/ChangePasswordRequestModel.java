package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequestModel {
    @SerializedName("srf_id")
    public String srf_id;

    @SerializedName("oldPassword")
    public String oldPassword;

    @SerializedName("newPassword")
    public String newPassword;

    public ChangePasswordRequestModel(String srf_id, String oldPassword, String newPassword) {
        this.srf_id = srf_id;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
