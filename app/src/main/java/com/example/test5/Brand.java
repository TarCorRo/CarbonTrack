package com.example.test5;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Brand {
    public String id;
    public String name;
    // Constructor gol necesar pentru Firebase
    public Brand() {
    }

    // Constructor cu parametri (opțional)
    public Brand(String id, String name) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Brand{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
