package com.huertos.parcel.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/** Estructura uniforme de error para toda la API. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        List<FieldIssue> details
) {
    public record FieldIssue(String field, String message) { }

    public static ErrorResponse of(ErrorCode code, String message, String path, List<FieldIssue> details) {
        return new ErrorResponse(Instant.now(), code.getStatus().value(), code.getStatus().getReasonPhrase(),
                code.name(), message, path, details);
    }
}
