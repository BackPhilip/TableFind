package com.example.tablefind.data_models;

public class RestaurantMenuItem
{
    private String objectId;
    private String restaurantId;
    private String name;
    private String type;
    private String ingredients;
    private Boolean outOfStock;
    private double price;

    public String getObjectId() { return objectId; }

    public void setObjectId(String objectId) { this.objectId = objectId; }

    public String getRestaurantId() { return restaurantId; }

    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getIngredients() { return ingredients; }

    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public Boolean getOutOfStock() { return outOfStock; }

    public void setOutOfStock(Boolean outOfStock) { this.outOfStock = outOfStock; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }
}
