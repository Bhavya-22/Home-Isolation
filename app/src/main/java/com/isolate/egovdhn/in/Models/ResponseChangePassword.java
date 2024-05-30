package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class ResponseChangePassword {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;
}
