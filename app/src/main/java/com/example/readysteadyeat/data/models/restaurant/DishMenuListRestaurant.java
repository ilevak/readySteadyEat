package com.example.readysteadyeat.data.models.restaurant;

public class DishMenuListRestaurant {

    public String category, dairyFree, description, glutenFree, name, price;

    public DishMenuListRestaurant(String category, String dairyFree, String description, String glutenFree, String name, String price) {
        this.category = category;
        this.dairyFree = dairyFree;
        this.description = description;
        this.glutenFree = glutenFree;
        this.name = name;
        this.price = price;
    }

    public DishMenuListRestaurant() {
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
}
