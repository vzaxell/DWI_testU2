package com.huertos.parcel.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Excepciones personalizadas del dominio. */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldIssue> details = List.of();
        if (ex instanceof InvalidParcelDataException ipd) {
            details = List.of(new ErrorResponse.FieldIssue(ipd.getField(), ipd.getMessage()));
        }
        return build(ex.getErrorCode(), ex.getMessage(), req, details);
    }

    /** Fallos de Bean Validation (@Valid en el body). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldIssue> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldIssue(fe.getField(), fe.getDefaultMessage()))
                .sorted(Comparator.comparing(ErrorResponse.FieldIssue::field))
                .toList();
        return build(ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.getDefaultMessage(), req, details);
    }

    /** JSON mal formado, tipos incorrectos o propiedades desconocidas. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldIssue> details = List.of();
        if (ex.getCause() instanceof UnrecognizedPropertyException upe) {
            details = List.of(new ErrorResponse.FieldIssue(upe.getPropertyName(), "Campo no permitido."));
        }
        return build(ErrorCode.MALFORMED_REQUEST, ErrorCode.MALFORMED_REQUEST.getDefaultMessage(), req, details);
    }

    /** UUID inválido en el path. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldIssue> details =
                List.of(new ErrorResponse.FieldIssue(ex.getName(), "Formato inválido."));
        return build(ErrorCode.INVALID_IDENTIFIER, ErrorCode.INVALID_IDENTIFIER.getDefaultMessage(), req, details);
    }

    /** Restricción única violada por una condición de carrera. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violación de integridad en {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return build(ErrorCode.DATA_CONFLICT, ErrorCode.DATA_CONFLICT.getDefaultMessage(), req, List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethod(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return build(ErrorCode.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED.getDefaultMessage(), req, List.of());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMedia(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        return build(ErrorCode.UNSUPPORTED_MEDIA_TYPE, ErrorCode.UNSUPPORTED_MEDIA_TYPE.getDefaultMessage(), req, List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
        return build(ErrorCode.RESOURCE_NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND.getDefaultMessage(), req, List.of());
    }

    /** Último recurso: el detalle va al log; al cliente solo un mensaje genérico. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Error inesperado en {} {}", req.getMethod(), req.getRequestURI(), ex);
        return build(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getDefaultMessage(), req, List.of());
    }

    private ResponseEntity<ErrorResponse> build(ErrorCode code, String message, HttpServletRequest req,
                                                List<ErrorResponse.FieldIssue> details) {
        return ResponseEntity.status(code.getStatus())
                .body(ErrorResponse.of(code, message, req.getRequestURI(), details));
    }
}
