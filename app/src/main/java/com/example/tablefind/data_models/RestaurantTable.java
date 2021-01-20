package com.example.tablefind.data_models;

import java.util.Date;

public class RestaurantTable
{
    private String objectId;
    private String restaurantId;
    private int capacity;
    private boolean available;
    private String tableInfo;
    private String name;
    private int xPos;
    private int yPos;

    public String getObjectId() { return objectId; }

    public void setObjectId(String objectId) { this.objectId = objectId; }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getTableInfo() { return tableInfo; }

    public void setTableInfo(String tableInfo) { this.tableInfo = tableInfo; }

    public int getCapacity() { return capacity; }

    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getxPos() { return xPos; }

    public void setxPos(int xPos) { this.xPos = xPos; }

    public int getyPos() { return yPos; }

    public void setyPos(int yPos) { this.yPos = yPos; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
