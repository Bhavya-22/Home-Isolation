package com.isolate.egovdhn.in.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseGetContacts {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("contacts")
    public ArrayList<EmergencyContact> contacts;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<EmergencyContact> getContacts() {
        return contacts;
    }

    public static class EmergencyContact{
        @SerializedName("name")
        public String name;

        @SerializedName("number")
        public String number;

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }
    }
}
