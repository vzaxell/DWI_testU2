package com.huertos.parcel.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record LocationRequest(
        @NotNull(message = "La latitud es obligatoria.")
        @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90 grados.")
        @DecimalMax(value = "90.0",  message = "La latitud debe estar entre -90 y 90 grados.")
        Double latitude,

        @NotNull(message = "La longitud es obligatoria.")
        @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180 grados.")
        @DecimalMax(value = "180.0",  message = "La longitud debe estar entre -180 y 180 grados.")
        Double longitude
) { }
