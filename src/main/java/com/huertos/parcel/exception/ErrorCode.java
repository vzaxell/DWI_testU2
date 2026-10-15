package com.huertos.parcel.exception;

import org.springframework.http.HttpStatus;

/** Catálogo central de errores: código estable + estado HTTP + mensaje por defecto. */
public enum ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Los datos enviados no son válidos. Revise los campos indicados."),
    MALFORMED_REQUEST(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud está mal formado o contiene campos no permitidos."),
    INVALID_IDENTIFIER(HttpStatus.BAD_REQUEST, "El identificador proporcionado no tiene un formato válido (se espera un UUID)."),
    PARCEL_NOT_FOUND(HttpStatus.NOT_FOUND, "La parcela solicitada no existe."),
    DUPLICATE_PARCEL(HttpStatus.CONFLICT, "Ya existe una parcela con ese nombre."),
    DATA_CONFLICT(HttpStatus.CONFLICT, "La operación entra en conflicto con datos existentes."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "El recurso solicitado no existe."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "El método HTTP no está permitido para este recurso."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de contenido no soportado. Use application/json."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno. Intente nuevamente más tarde.");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus()     { return status; }
    public String getDefaultMessage() { return defaultMessage; }
}
