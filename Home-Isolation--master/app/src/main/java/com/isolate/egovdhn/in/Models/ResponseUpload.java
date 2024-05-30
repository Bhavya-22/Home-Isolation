package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class ResponseUpload {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("token")
    public String token;

    public ResponseUpload(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
