package com.pluralsight.capstones;

import java.sql.Time;
import java.util.Date;

public class Transactions {
    private final Date date;
    private final Time time;
    private final String description;
    private final String vendor;
    private final float amount;

    public Transactions(Date date, Time time, String description, String vendor, float amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public float getAmount() {
        return amount;
    }
}