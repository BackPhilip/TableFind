package com.example.tablefind.data_models;

import java.util.Date;

public class Reservation
{
    private String objectId;
    private String tableId;
    private Date takenFrom;
    private Date takenTo;
    private String userId;
    private String name;
    private String number;
    private String restaurantId;
    private Date created;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTableId() { return tableId; }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Date getTakenFrom() { return takenFrom; }

    public void setTakenFrom(Date takenFrom) {
        this.takenFrom = takenFrom;
    }

    public Date getTakenTo() { return takenTo; }

    public void setTakenTo(Date takenTo) {
        this.takenTo = takenTo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getNumber() { return number; }

    public void setNumber(String number) { this.number = number; }

    public String getRestaurantId() { return restaurantId; }

    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public Date getCreated() { return created; }

    public void setCreated(Date created) { this.created = created; }
}
