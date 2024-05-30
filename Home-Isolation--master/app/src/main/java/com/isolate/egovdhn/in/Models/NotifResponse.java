
package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NotifResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("notifications")
    @Expose
    private ArrayList<NotifModel> notifModels = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<NotifModel> getNotifModels() {
        return notifModels;
    }

    public void setNotifModels(ArrayList<NotifModel> notifModels) {
        this.notifModels = notifModels;
    }
}
