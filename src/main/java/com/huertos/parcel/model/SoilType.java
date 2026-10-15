package com.huertos.parcel.model;

import com.huertos.parcel.exception.InvalidParcelDataException;

import java.util.Locale;

public enum SoilType {
    ARCILLOSO, LIMOSO, ARENOSO;

    /** Convierte texto (ya sanitizado) al enum; lanza excepción de negocio si no es válido. */
    public static SoilType fromText(String text) {
        if (text == null) {
            throw new InvalidParcelDataException("soilType",
                    "El tipo de suelo es obligatorio. Valores permitidos: arcilloso, limoso, arenoso.");
        }
        try {
            return SoilType.valueOf(text.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new InvalidParcelDataException("soilType",
                    "Tipo de suelo no reconocido. Valores permitidos: arcilloso, limoso, arenoso.");
        }
    }
}
