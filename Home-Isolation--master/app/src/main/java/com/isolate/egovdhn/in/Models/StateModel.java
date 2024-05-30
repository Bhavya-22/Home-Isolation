package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class StateModel {
    @SerializedName("sId")
    String stateId;

    @SerializedName("sName")
    String stateName;

    public StateModel(String stateId) {
        this.stateId = stateId;
    }

    public StateModel(String stateId, String stateName) {
        this.stateId = stateId;
        this.stateName = stateName;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
