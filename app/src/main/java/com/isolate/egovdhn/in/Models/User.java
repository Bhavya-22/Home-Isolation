
package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("srf_id")
    @Expose
    private String srfId;
    @SerializedName("test_date")
    @Expose
    private String testDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("block_id")
    @Expose
    private String blockId;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("caretaker_name")
    @Expose
    private String caretakerName;
    @SerializedName("caretaker_phone")
    @Expose
    private String caretakerPhone;
    @SerializedName("rooms")
    @Expose
    private String rooms;
    @SerializedName("washrooms")
    @Expose
    private String washrooms;
    @SerializedName("attached_washroom")
    @Expose
    private String attachedWashroom;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("doctor")
    @Expose
    private String doctor;
    @SerializedName("family_total")
    @Expose
    private String familyTotal;
    @SerializedName("family_above_60")
    @Expose
    private String familyAbove60;
    @SerializedName("family_below_10")
    @Expose
    private String familyBelow10;
    @SerializedName("was_hospitalised")
    @Expose
    private String wasHospitalised;
    @SerializedName("diseases")
    @Expose
    private String diseases;
    @SerializedName("device_id")
    @Expose
    private String deviceId;

    public String getSrfId() {
        return srfId;
    }

    public void setSrfId(String srfId) {
        this.srfId = srfId;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCaretakerName() {
        return caretakerName;
    }

    public void setCaretakerName(String caretakerName) {
        this.caretakerName = caretakerName;
    }

    public String getCaretakerPhone() {
        return caretakerPhone;
    }

    public void setCaretakerPhone(String caretakerPhone) {
        this.caretakerPhone = caretakerPhone;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getWashrooms() {
        return washrooms;
    }

    public void setWashrooms(String washrooms) {
        this.washrooms = washrooms;
    }

    public String getAttachedWashroom() {
        return attachedWashroom;
    }

    public void setAttachedWashroom(String attachedWashroom) {
        this.attachedWashroom = attachedWashroom;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getFamilyTotal() {
        return familyTotal;
    }

    public void setFamilyTotal(String familyTotal) {
        this.familyTotal = familyTotal;
    }

    public String getFamilyAbove60() {
        return familyAbove60;
    }

    public void setFamilyAbove60(String familyAbove60) {
        this.familyAbove60 = familyAbove60;
    }

    public String getFamilyBelow10() {
        return familyBelow10;
    }

    public void setFamilyBelow10(String familyBelow10) {
        this.familyBelow10 = familyBelow10;
    }

    public String getWasHospitalised() {
        return wasHospitalised;
    }

    public void setWasHospitalised(String wasHospitalised) {
        this.wasHospitalised = wasHospitalised;
    }

    public String getDiseases() {
        return diseases;
    }

    public void setDiseases(String diseases) {
        this.diseases = diseases;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
