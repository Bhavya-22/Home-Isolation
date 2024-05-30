package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class TelemedicineScheduleModel {
    @SerializedName("telemedicine_id")
    public String telemedicine_id;

    @SerializedName("patient_id")
    public int patient_id;

    @SerializedName("datetime")
    public String datetime;

    @SerializedName("link")
    public String link;

    @SerializedName("doctor")
    public String doctor;

    public TelemedicineScheduleModel(String telemedicine_id, int patient_id, String datetime, String link, String doctor) {
        this.telemedicine_id = telemedicine_id;
        this.patient_id = patient_id;
        this.datetime = datetime;
        this.link = link;
        this.doctor = doctor;
    }
}
