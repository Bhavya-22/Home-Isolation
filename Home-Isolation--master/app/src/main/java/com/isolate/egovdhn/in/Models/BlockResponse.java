package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BlockResponse {
    @SerializedName("status")
    int status;

    @SerializedName("message")
    String message;

    @SerializedName("blocks")
    ArrayList<BlockModel> blocksList;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<BlockModel> getBlocksList() {
        return blocksList;
    }
}
