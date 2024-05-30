package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseGetPrescription {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("prescriptions")
    public ArrayList<PrescriptionModel> prescriptions;

    public ResponseGetPrescription(int status, String message, ArrayList<PrescriptionModel> prescriptions) {
        this.status = status;
        this.message = message;
        this.prescriptions = prescriptions;
    }
}
