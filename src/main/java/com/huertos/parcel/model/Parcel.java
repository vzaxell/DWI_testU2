package com.huertos.parcel.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "parcels",
       uniqueConstraints = @UniqueConstraint(name = "uk_parcel_name", columnNames = "name_normalized"))
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    /** Nombre en minúsculas: garantiza unicidad sin distinguir mayúsculas, incluso ante condiciones de carrera. */
    @Column(name = "name_normalized", nullable = false, length = 100)
    private String nameNormalized;

    @Embedded
    private GpsLocation location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SoilType soilType;

    @Column(nullable = false, length = 100)
    private String ownerOrLead;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Parcel() { }

    public Parcel(String name, GpsLocation location, SoilType soilType, String ownerOrLead) {
        this.name = name;
        this.nameNormalized = name.toLowerCase(Locale.ROOT);
        this.location = location;
        this.soilType = soilType;
        this.ownerOrLead = ownerOrLead;
        this.createdAt = Instant.now();
    }

    public UUID getId()              { return id; }
    public String getName()          { return name; }
    public GpsLocation getLocation() { return location; }
    public SoilType getSoilType()    { return soilType; }
    public String getOwnerOrLead()   { return ownerOrLead; }
    public Instant getCreatedAt()    { return createdAt; }
}
