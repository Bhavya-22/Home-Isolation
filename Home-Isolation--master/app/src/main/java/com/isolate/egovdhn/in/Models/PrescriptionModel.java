package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class PrescriptionModel {
    @SerializedName("prescription_id")
    public String prescription_id;

    @SerializedName("patient_id")
    public String patient_id;

    @SerializedName("date")
    public String date;

    @SerializedName("prescription")
    public String prescription;

    @SerializedName("remarks")
    public String remarks;

    public PrescriptionModel(String prescription_id, String patient_id, String date, String prescription, String remarks) {
        this.prescription_id = prescription_id;
        this.patient_id = patient_id;
        this.date = date;
        this.prescription = prescription;
        this.remarks = remarks;
    }
}
