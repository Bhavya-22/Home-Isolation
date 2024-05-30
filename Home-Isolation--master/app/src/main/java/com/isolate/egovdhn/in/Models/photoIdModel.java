package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class photoIdModel {
    @SerializedName("photo_id")
    public String photo_id;

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }
}
