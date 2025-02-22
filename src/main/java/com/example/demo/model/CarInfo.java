package com.example.demo.model;

public class CarInfo {
    private String vin;
    private String make;
    private String model;
    private int year;

    public CarInfo(String vin, String make, String model, int year) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public String getVin() { return vin; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
}