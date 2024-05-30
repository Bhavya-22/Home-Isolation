
package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadModel {

    @SerializedName("upload_id")
    @Expose
    private String uploadId;
    @SerializedName("patient_id")
    @Expose
    private String patientId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
