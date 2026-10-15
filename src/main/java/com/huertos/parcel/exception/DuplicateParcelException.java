package com.huertos.parcel.exception;

public class DuplicateParcelException extends BusinessException {
    public DuplicateParcelException(String name) {
        super(ErrorCode.DUPLICATE_PARCEL, "Ya existe una parcela registrada con el nombre '" + name + "'.");
    }
}
