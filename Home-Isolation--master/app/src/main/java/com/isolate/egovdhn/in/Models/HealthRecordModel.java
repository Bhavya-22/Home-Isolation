package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class HealthRecordModel {
    @SerializedName("timestamp")
    public String timestamp;

    @SerializedName("temperature")
    public Float temperature;

    @SerializedName("pulse_rate")
    public Float pulse;

    @SerializedName("bp_high")
    public Float bp_high;

    @SerializedName("bp_low")
    public Float bp_low;

    @SerializedName("spo2")
    public Float spo2;

    @SerializedName("respiratory_rate")
    public Float respiratory_rate;
}
