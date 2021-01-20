package com.example.tablefind.data_models;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

import java.sql.Blob;

public class Restaurant
{
    private String name;
    private String objectId;
    private String locationString;
    private String locationGPS;
    private String ownerId;
    private String menuLink;
    private String contactNumber;
    private int maxCapacity;

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String location) {
        this.locationString = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getLocationGPS() { return locationGPS; }

    public void setLocationGPS(String locationGPS) { this.locationGPS = locationGPS; }

    public String getOwnerId() { return ownerId; }

    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getMenuLink() { return menuLink; }

    public void setMenuLink(String menuLink) { this.menuLink = menuLink; }

    public String getContactNumber() { return contactNumber; }

    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public int getMaxCapacity() { return maxCapacity; }

    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
}
