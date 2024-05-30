package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IsolationRequestModel implements Serializable {

    private final static long serialVersionUID = -9093231802518363731L;
    @SerializedName("srf_id")
    @Expose
    private String srfId;
    @SerializedName("rooms")
    @Expose
    private Long rooms;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("block_id")
    @Expose
    private String blockId;
    @SerializedName("caretaker_name")
    @Expose
    private String caretakerName;
    @SerializedName("hasAttached")
    @Expose
    private Long hasAttached;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("washrooms")
    @Expose
    private Long washrooms;
    @SerializedName("caretaker_phone")
    @Expose
    private String caretakerPhone;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("test_date")
    @Expose
    private String testDate;

    /**
     * No args constructor for use in serialization
     */
    public IsolationRequestModel() {
    }

    /**
     * @param rooms
     * @param address
     * @param caretakerName
     * @param latitude
     * @param srfId
     * @param hasAttached
     * @param blockId
     * @param phone
     * @param dob
     * @param name
     * @param washrooms
     * @param caretakerPhone
     * @param testDate
     * @param longitude
     */
    public IsolationRequestModel(String srfId, Long rooms, String address, Double latitude, String blockId, String caretakerName, Long hasAttached, String phone, String dob, String name, Long washrooms, String caretakerPhone, Double longitude, String testDate) {
        super();
        this.srfId = srfId;
        this.rooms = rooms;
        this.address = address;
        this.latitude = latitude;
        this.blockId = blockId;
        this.caretakerName = caretakerName;
        this.hasAttached = hasAttached;
        this.phone = phone;
        this.dob = dob;
        this.name = name;
        this.washrooms = washrooms;
        this.caretakerPhone = caretakerPhone;
        this.longitude = longitude;
        this.testDate = testDate;
    }

    public String getSrfId() {
        return srfId;
    }

    public void setSrfId(String srfId) {
        this.srfId = srfId;
    }

    public Long getRooms() {
        return rooms;
    }

    public void setRooms(Long rooms) {
        this.rooms = rooms;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getCaretakerName() {
        return caretakerName;
    }

    public void setCaretakerName(String caretakerName) {
        this.caretakerName = caretakerName;
    }

    public Long getHasAttached() {
        return hasAttached;
    }

    public void setHasAttached(Long hasAttached) {
        this.hasAttached = hasAttached;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWashrooms() {
        return washrooms;
    }

    public void setWashrooms(Long washrooms) {
        this.washrooms = washrooms;
    }

    public String getCaretakerPhone() {
        return caretakerPhone;
    }

    public void setCaretakerPhone(String caretakerPhone) {
        this.caretakerPhone = caretakerPhone;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

}