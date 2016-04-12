package com.example.shubhampandey.parceldrop.util;

/**
 * Created by SHUBHAM PANDEY on 3/6/2016.
 */
public class Parcel {
    private DeliveryMethod deliveryMethod;
    private int perKmCharge;
    private ParcelType type;
    private int weight;
    private int weightCharge;

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public int getPerKmCharge() {
        return perKmCharge;
    }

    public void setPerKmCharge(int perKmCharge) {
        this.perKmCharge = perKmCharge;
    }

    public ParcelType getType() {
        return type;
    }

    public void setType(ParcelType type) {
        this.type = type;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWeightCharge() {
        return weightCharge;
    }

    public void setWeightCharge(int weightCharge) {
        this.weightCharge = weightCharge;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    private double distance;
    private double totalPrice;
}
