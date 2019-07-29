package org.d3ifcool.alert.model;

/**
 * Created by Sholeh Hermawan on 10/31/2017.
 */

public class Location {
    public String dateOfLocation, nameOfLocation, timeOfLocation, idLocationList;
    public double latitude, longitude;

    public Location() {
    }

    public Location(Location location, String idLocationList) {
        this.dateOfLocation = location.getDateOfLocation();
        this.nameOfLocation = location.getNameOfLocation();
        this.timeOfLocation = location.getTimeOfLocation();
        this.idLocationList = idLocationList;
    }

    public Location(String dateOfLocation, String nameOfLocation, String timeOfLocation, double latitude, double longitude) {
        this.dateOfLocation = dateOfLocation;
        this.nameOfLocation = nameOfLocation;
        this.timeOfLocation = timeOfLocation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDateOfLocation() {
        return dateOfLocation;
    }

    public String getNameOfLocation() {
        return nameOfLocation;
    }

    public String getTimeOfLocation() {
        return timeOfLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
