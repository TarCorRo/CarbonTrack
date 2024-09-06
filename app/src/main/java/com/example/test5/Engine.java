package com.example.test5;

public class Engine {
    public Double co2Emissions;
    public Double city;
    public Double combined;
    public String fuel;
    public Double highway;
    public String id;
    public String name;
    public String automobile_id;

    // Constructor
    public Engine() {
        // Constructor implicit necesar pentru Firebase
    }

    public Engine(Double co2Emissions, Double city, Double combined, String fuel, Double highway, String id, String name, String automobile_id) {
        this.co2Emissions = co2Emissions;
        this.city = city;
        this.combined = combined;
        this.fuel = fuel;
        this.highway = highway;
        this.id = id;
        this.name = name;
        this.automobile_id = automobile_id;
    }

    // Getters and Setters
    public Double getco2Emissions() {
        return co2Emissions;
    }

    public void setco2Emissions(Double co2Emissions) {
        this.co2Emissions = co2Emissions;
    }

    public Double getCity() {
        return city;
    }

    public void setCity(Double city) {
        this.city = city;
    }

    public Double getCombined() {
        return combined;
    }

    public void setCombined(Double combined) {
        this.combined = combined;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public Double getHighway() {
        return highway;
    }

    public void setHighway(Double highway) {
        this.highway = highway;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAutomobile_id() {
        return automobile_id;
    }

    public void setAutomobile_id(String automobile_id) {
        this.automobile_id = automobile_id;
    }

    @Override
    public String toString() {
        return "Engine{" +
                "CO2Emissions=" + co2Emissions +
                ", city=" + city +
                ", combined=" + combined +
                ", fuel='" + fuel + '\'' +
                ", highway=" + highway +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", automobile_id='" + automobile_id + '\'' +
                '}';
    }
}
