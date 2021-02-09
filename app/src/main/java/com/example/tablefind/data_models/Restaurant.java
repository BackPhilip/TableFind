package com.example.tablefind.data_models;
import java.util.Date;

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
    private Date open;
    private Date close;

    public Restaurant() { }

    public Restaurant(String name, String objectId, String locationString, String locationGPS, String ownerId, String menuLink, String contactNumber, int maxCapacity, Date open, Date close) {
        this.name = name;
        this.objectId = objectId;
        this.locationString = locationString;
        this.locationGPS = locationGPS;
        this.ownerId = ownerId;
        this.menuLink = menuLink;
        this.contactNumber = contactNumber;
        this.maxCapacity = maxCapacity;
        this.open = open;
        this.close = close;
    }

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

    public Date getOpen() { return open; }

    public void setOpen(Date open) { this.open = open; }

    public Date getClose() { return close; }

    public void setClose(Date close) { this.close = close; }
}
