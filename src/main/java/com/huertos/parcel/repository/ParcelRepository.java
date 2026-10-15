package com.huertos.parcel.repository;

import com.huertos.parcel.model.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParcelRepository extends JpaRepository<Parcel, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
