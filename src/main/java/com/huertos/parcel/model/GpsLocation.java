package com.huertos.parcel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GpsLocation {

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    protected GpsLocation() { }

    public GpsLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude()  { return latitude; }
    public Double getLongitude() { return longitude; }
}
