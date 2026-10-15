package com.huertos.parcel.dto;

import com.huertos.parcel.model.Parcel;

/** Detalle de la parcela + estado actual (por ahora sin lecturas de sensores asociadas). */
public record ParcelDetailResponse(ParcelResponse parcel, String currentStatus, String statusNote) {

    public static ParcelDetailResponse from(Parcel p) {
        return new ParcelDetailResponse(
                ParcelResponse.from(p),
                "SIN_LECTURAS",
                "Aún no se han recibido lecturas de sensores para esta parcela.");
    }
}
