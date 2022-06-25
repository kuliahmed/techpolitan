package com.ahmad.techpolitan;

import java.util.ArrayList;

public class HistoryObject {
    private String status;
    private String signInTime;
    private String signOutTime;
    private String locationIn;
    private String locationOut;
    private String type;
    private String name;
    private String nip;
    private String date;

    public HistoryObject(String status, String signInTime, String signOutTime, String locationIn,
                         String locationOut, String type, String name, String nip, String date) {
        this.status = status;
        this.signInTime = signInTime;
        this.signOutTime = signOutTime;
        this.locationIn = locationIn;
        this.locationOut = locationOut;
        this.type = type;
        this.name = name;
        this.nip = nip;
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(String signInTime) {
        this.signInTime = signInTime;
    }

    public String getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(String signOutTime) {
        this.signOutTime = signOutTime;
    }

    public String getLocationIn() {
        return locationIn;
    }

    public void setLocationIn(String locationIn) {
        this.locationIn = locationIn;
    }

    public String getLocationOut() {
        return locationOut;
    }

    public void setLocationOut(String locationOut) {
        this.locationOut = locationOut;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static int getLastContactId() {
        return lastContactId;
    }

    public static void setLastContactId(int lastContactId) {
        HistoryObject.lastContactId = lastContactId;
    }

    private static int lastContactId = 0;

    public static ArrayList<HistoryObject> createHistoryObjects(int historyLenght) {
        ArrayList<HistoryObject> historyObjects = new ArrayList<HistoryObject>();

        for (int i = 1; i <= historyLenght; i++) {
            if (i < 6)
                historyObjects.add(new HistoryObject("APPROVED", "08:00:56", "18:23:34", "Home, Bambu Apus Pamulang", "Home, Bambu Apus Pamulang", "Work From Office (WFO)", "Rizky Ahmad Darmawan", "131011402651", "0" + i + " July 2022"));
            else
                historyObjects.add(new HistoryObject("APPROVED", "08:00:56", "18:23:34", "Home, Bambu Apus Pamulang", "Home, Bambu Apus Pamulang", "Work From Office (WFO)", "Rizky Ahmad Darmawan", "131011402651", i + " July 2022"));
        }

        return historyObjects;
    }
}
