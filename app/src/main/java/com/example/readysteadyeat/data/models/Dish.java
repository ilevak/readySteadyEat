package com.example.readysteadyeat.data.models;

public class Dish {
    public String category;
    public String dairyFree;
    public String description;
    public String glutenFree;
    public String name;
    public String price;
    public String restaurantId;
    public String imgUrl;


    public Dish() {}

    public Dish(String category, String dairyFree,
                 String description, String glutenFree,
                 String name, String price,
                 String restaurantId, String imgUrl) {

        this.category = category;
        this.dairyFree = dairyFree;
        this.description = description;
        this.glutenFree = glutenFree;
        this.name = name;
        this.price = price;
        this.restaurantId = restaurantId;
        this.imgUrl = imgUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDairyFree() {
        return dairyFree;
    }

    public void setDairyFree(String dairyFree) {
        this.dairyFree = dairyFree;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGlutenFree() {
        return glutenFree;
    }

    public void setGlutenFree(String glutenFree) {
        this.glutenFree = glutenFree;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
