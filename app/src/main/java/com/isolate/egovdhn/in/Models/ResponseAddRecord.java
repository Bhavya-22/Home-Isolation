package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class ResponseAddRecord {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    public ResponseAddRecord(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
