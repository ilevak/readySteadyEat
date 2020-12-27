package com.example.readysteadyeat.data.models;

public class Restaurant {

    public String userId;
    public String email;
    public String name;
    public String state;
    public String city;

    public String street;
    public String houseNumber;
    public String iban;
    public boolean userType;
    public String imgUrl;

    public Restaurant() {}

    public Restaurant(String userId, String name, String email,
                      String state, String city,
                      String street, String houseNumber,
                      String iban, boolean userType,
                      String imgUrl) {

        this.userId = userId;
        this.email = email;
        this.name = name;
        this.state = state;
        this.street = street;
        this.city = city;
        this.houseNumber = houseNumber;
        this.iban = iban;
        this.userType = userType;
        this.imgUrl = imgUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public boolean isUserType() {
        return userType;
    }

    public void setUserType(boolean userType) {
        this.userType = userType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
