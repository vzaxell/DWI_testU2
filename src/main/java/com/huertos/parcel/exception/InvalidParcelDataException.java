package com.huertos.parcel.exception;

/** Violación de reglas de negocio sobre los datos de una parcela. */
public class InvalidParcelDataException extends BusinessException {

    private final String field;

    public InvalidParcelDataException(String field, String message) {
        super(ErrorCode.VALIDATION_ERROR, message);
        this.field = field;
    }

    public String getField() { return field; }
}
