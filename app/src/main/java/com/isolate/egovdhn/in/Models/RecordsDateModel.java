package com.isolate.egovdhn.in.Models;

import java.util.ArrayList;
import java.util.Date;

public class RecordsDateModel {
    public Date date;
    public ArrayList<HealthRecordModel> singleDateRecords;

    public RecordsDateModel(Date date) {
        this.date = date;
        singleDateRecords = new ArrayList<>();
    }
}
