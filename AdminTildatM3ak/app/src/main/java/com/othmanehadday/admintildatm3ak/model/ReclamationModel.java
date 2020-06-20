package com.othmanehadday.admintildatm3ak.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ReclamationModel implements Serializable {
    private String id;
    private String fullName;
    private String phone;
    private String violenceCategorie;
    private String address;
    private Date currentDate;
    private boolean seen;

    public ReclamationModel() {
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {

        this.currentDate = currentDate;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getViolenceCategorie() {
        return violenceCategorie;
    }

    public void setViolenceCategorie(String violenceCategorie) {
        this.violenceCategorie = violenceCategorie;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ReclamationModel{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", violenceCategorie='" + violenceCategorie + '\'' +
                ", address='" + address + '\'' +
                ", currentDate=" + currentDate +
                '}';
    }
}
