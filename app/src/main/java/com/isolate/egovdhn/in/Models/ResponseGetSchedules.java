package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseGetSchedules {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("schedules")
    public ArrayList<TelemedicineScheduleModel> schedules;

    public ResponseGetSchedules(int status, String message, ArrayList<TelemedicineScheduleModel> schedules) {
        this.status = status;
        this.message = message;
        this.schedules = schedules;
    }
}
