package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class NotifModel {
    @SerializedName("notification_id")
    public String notification_id;
    @SerializedName("patient_id")
    public String patient_id;
    @SerializedName("type")
    public String type;
    @SerializedName("content")
    public String content;
    @SerializedName("timestamp")
    public String timestamp;

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
