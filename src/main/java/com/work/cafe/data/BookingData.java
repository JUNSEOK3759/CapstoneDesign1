package com.work.cafe.data;

import java.io.Serializable;

public class BookingData implements Serializable {


    public int personnel;
    public String nickname;
    public String cafeID;
    public String userEmail;
    public int scheduled;
    public boolean isAccept = false;

    public BookingData(){}

    public BookingData(int personnel, String nickname, String cafeID, String userEmail, int scheduled, boolean isAccept) {
        this.personnel = personnel;
        this.nickname = nickname;
        this.cafeID = cafeID;
        this.userEmail = userEmail;
        this.scheduled = scheduled;
        this.isAccept = isAccept;
    }

    public int getPersonnel() {
        return personnel;
    }

    public void setPersonnel(int personnel) {
        this.personnel = personnel;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCafeID() {
        return cafeID;
    }

    public void setCafeID(String cafeID) {
        this.cafeID = cafeID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getScheduled() {
        return scheduled;
    }

    public void setScheduled(int scheduled) {
        this.scheduled = scheduled;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }
}
