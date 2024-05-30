package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseGetRecords {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("records")
    public ArrayList<HealthRecordModel> records;

    public ResponseGetRecords(int status, String message, ArrayList<HealthRecordModel> records) {
        this.status = status;
        this.message = message;
        this.records = records;
    }
}
