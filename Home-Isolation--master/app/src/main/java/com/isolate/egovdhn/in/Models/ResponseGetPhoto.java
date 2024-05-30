package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseGetPhoto {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("id")
    public List<photoIdModel> idModelList;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List< photoIdModel > getIdModelList() {
        return idModelList;
    }

    public void setIdModelList(List< photoIdModel > idModelList) {
        this.idModelList = idModelList;
    }
}
