package com.pluralsight.capstones;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transactions {
    private final LocalDate date;
    private final LocalTime time;
    private final String description;
    private final String vendor;
    private final float amount;

    public Transactions(LocalDate date, LocalTime time, String description, String vendor, float amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
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