package com.isolate.egovdhn.in.Models;

public class UserModel {
    String srfId;

    public UserModel(String srfId) {
        this.srfId = srfId;
    }

    public String getSrfId() {
        return srfId;
    }

    public void setSrfId(String srfId) {
        this.srfId = srfId;
    }
}
