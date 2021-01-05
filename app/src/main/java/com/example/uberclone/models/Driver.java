package com.example.uberclone.models;

public class Driver  {

    String name;
    String email;
    String id;
    String vehicleBrand;
    String vehiclePlate;


    public Driver(String name, String email, String id, String vehicleBrand, String vehiclePlate) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.vehicleBrand = vehicleBrand;
        this.vehiclePlate = vehiclePlate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }
}
