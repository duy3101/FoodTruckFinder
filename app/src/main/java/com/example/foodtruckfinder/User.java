package com.example.foodtruckfinder;

public class User {
    public String email;
    public String first_name;
    public String last_name;
    public String phone;
    public String role;

    public User() {

    }

    public User(String first_name, String last_name, String phone)
    {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
    }

    public User(String email, String first_name, String last_name, String phone, String role)
    {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.role = role;
    }

    public void setInfo(String first_name, String last_name, String phone)
    {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
    }
}
