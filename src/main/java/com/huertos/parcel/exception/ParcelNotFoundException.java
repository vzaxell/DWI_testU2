package com.huertos.parcel.exception;

import java.util.UUID;

public class ParcelNotFoundException extends BusinessException {
    public ParcelNotFoundException(UUID id) {
        super(ErrorCode.PARCEL_NOT_FOUND, "No existe una parcela con el identificador " + id + ".");
    }
}
