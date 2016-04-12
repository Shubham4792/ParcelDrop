package com.example.shubhampandey.parceldrop.util;

/**
 * Created by SHUBHAM PANDEY on 3/6/2016.
 */
public class ParcelType {
    public static String DOC = "Document";
    public static String FOOD = "Food Items";
    public static String OTHERS = "Others";
    private int docCharge;
    private int foodCharge;
    private int otherCharge;
    private String parcelType ="";
    private int acceptedCharge;

    public int getDocCharge() {
        return docCharge;
    }

    public void setDocCharge(int docCharge) {
        this.docCharge = docCharge;
    }

    public int getFoodCharge() {
        return foodCharge;
    }

    public void setFoodCharge(int foodCharge) {
        this.foodCharge = foodCharge;
    }

    public int getOtherCharge() {
        return otherCharge;
    }

    public void setOtherCharge(int otherCharge) {
        this.otherCharge = otherCharge;
    }

    public String getParcelType() {
        return parcelType;
    }

    public void setParcelType(String parcelType) {
        this.parcelType = parcelType;
    }

    public int getAcceptedCharge(String delMode) {
        if (delMode.equals(FOOD)) {
            acceptedCharge = getFoodCharge();
        } else if (delMode.equals(DOC)) {
            acceptedCharge = getDocCharge();
        } else {
            acceptedCharge = getOtherCharge();
        }
        return acceptedCharge;
    }
}
