package com.example.shubhampandey.parceldrop.util;

/**
 * Created by SHUBHAM PANDEY on 3/6/2016.
 */
public class DeliveryMethod {
    private int sameDayCharge;
    private int standardCharge;
    private int flexiCharge;
    public static String SAME_DAY = "Now";
    public static String STANDARD = "Standard";
    public static String FLEXI = "Flexible";
    private String delMode="";
    private int acceptedCharge;

    public int getSameDayCharge() {
        return sameDayCharge;
    }

    public void setSameDayCharge(int sameDayCharge) {
        this.sameDayCharge = sameDayCharge;
    }

    public int getStandardCharge() {
        return standardCharge;
    }

    public void setStandardCharge(int standardCharge) {
        this.standardCharge = standardCharge;
    }

    public int getFlexiCharge() {
        return flexiCharge;
    }

    public void setFlexiCharge(int flexiCharge) {
        this.flexiCharge = flexiCharge;
    }

    public String getDelMode() {
        return delMode;
    }

    public void setDelMode(String delMode) {
        this.delMode = delMode;
    }

    public int getAcceptedCharge(String delMode) {
        if (delMode.equals(SAME_DAY)) {
            acceptedCharge = getSameDayCharge();
        } else if (delMode.equals(STANDARD)) {
            acceptedCharge = getStandardCharge();
        } else {
            acceptedCharge = getFlexiCharge();
        }
        return acceptedCharge;
    }

}

