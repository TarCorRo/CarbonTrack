package com.example.test5;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Automobile {
    public String id;
    public String brand_id;
    public String name;

    // Constructor gol necesar pentru Firebase
    public Automobile() {
    }

    // Constructor cu parametri (opțional)
    public Automobile(String id, String brand_id, String name) {
        this.id = id;
        this.brand_id = brand_id;
        this.name = name;
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

    public String getBrandId() {
        return brand_id;
    }

    public void setBrandId(String brand_id) {
        this.brand_id = brand_id;
    }

    @Override
    public String toString() {
        return "Automobiles{" +
                "id='" + id + '\'' +
                ", brand_id='" + brand_id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

