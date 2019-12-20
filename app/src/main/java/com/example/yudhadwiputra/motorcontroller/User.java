package com.example.yudhadwiputra.motorcontroller;

public class User {
    private int id;
    private String username, email, phoneNumber, address;

    public User(int id, String username, String email, String phoneNumber, String address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }
}
