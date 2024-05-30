package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

public class BlockModel {
    @SerializedName("bmId")
    String blockId;

    @SerializedName("sId")
    String stateId;

    @SerializedName("dId")
    String districtId;

    @SerializedName("bmName")
    String blockName;

    public BlockModel(String stateId, String districtId) {
        this.stateId = stateId;
        this.districtId = districtId;
    }

    public String getStateId() {
        return stateId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public String getBlockId() {
        return blockId;
    }

    public String getBlockName() {
        return blockName;
    }
}
