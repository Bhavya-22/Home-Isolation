
package com.isolate.egovdhn.in.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("uploads")
    @Expose
    private List< UploadModel > uploads = null;

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

    public List< UploadModel > getUploads() {
        return uploads;
    }

    public void setUploads(List< UploadModel > uploads) {
        this.uploads = uploads;
    }

}
