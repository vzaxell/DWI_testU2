package com.huertos.parcel.dto;

import com.huertos.parcel.model.Parcel;

import java.time.Instant;
import java.util.UUID;

public record ParcelResponse(
        UUID id,
        String name,
        LocationRequest location,
        String soilType,
        String ownerOrLead,
        Instant createdAt
) {
    public static ParcelResponse from(Parcel p) {
        return new ParcelResponse(
                p.getId(),
                p.getName(),
                new LocationRequest(p.getLocation().getLatitude(), p.getLocation().getLongitude()),
                p.getSoilType().name().toLowerCase(),
                p.getOwnerOrLead(),
                p.getCreatedAt());
    }
}
