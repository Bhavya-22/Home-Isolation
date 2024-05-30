package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StateResponse {
    @SerializedName("status")
    int status;

    @SerializedName("message")
    String message;

    @SerializedName("states")
    ArrayList<StateModel> statesList;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<StateModel> getStatesList() {
        return statesList;
    }
}
