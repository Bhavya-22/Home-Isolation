package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class ResponseResetPassword {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
