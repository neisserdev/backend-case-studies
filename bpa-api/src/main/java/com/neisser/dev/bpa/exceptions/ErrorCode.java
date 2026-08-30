package com.neisser.dev.bpa.exceptions;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    VALIDATION_ERROR("Error de validación", HttpStatus.BAD_REQUEST),
    MALFORMED_REQUEST("Petición mal formada", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH("Parámetro con tipo incorrecto", HttpStatus.BAD_REQUEST),
    CONSTRAINT_VIOLATION("Restricción no satisfecha", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED("No autenticado", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("Credenciales inválidas", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("Refresh token inválido", HttpStatus.UNAUTHORIZED),
    ACCOUNT_UNAVAILABLE("Cuenta no disponible", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("Acceso denegado", HttpStatus.FORBIDDEN),
    ORIGIN_NOT_ALLOWED("Origen no autorizado", HttpStatus.FORBIDDEN),

    RESOURCE_NOT_FOUND("Recurso no encontrado", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_REGISTERED("Email ya registrado", HttpStatus.CONFLICT),
    RESOURCE_CONFLICT("Conflicto con el estado actual del recurso", HttpStatus.CONFLICT),
    BUSINESS_RULE_VIOLATION("Regla de negocio no satisfecha", HttpStatus.UNPROCESSABLE_ENTITY),

    ENDPOINT_NOT_FOUND("Endpoint no encontrado", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("Método HTTP no permitido", HttpStatus.METHOD_NOT_ALLOWED),
    NOT_ACCEPTABLE("Formato de respuesta no aceptable", HttpStatus.NOT_ACCEPTABLE),
    UNSUPPORTED_MEDIA_TYPE("Tipo de contenido no soportado", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    PAYLOAD_TOO_LARGE("Cuerpo de la petición demasiado grande", HttpStatus.PAYLOAD_TOO_LARGE),
    TOO_MANY_REQUESTS("Demasiadas peticiones", HttpStatus.TOO_MANY_REQUESTS),
    SERVICE_UNAVAILABLE("Servicio no disponible", HttpStatus.SERVICE_UNAVAILABLE),

    INTERNAL_ERROR("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String title;
    private final HttpStatus httpStatus;

    ErrorCode(String title, HttpStatus httpStatus) {
        this.title = title;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return name();
    }

    public String getTitle() {
        return title;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    private static final Map<Integer, ErrorCode> POR_STATUS = Stream.of(
            MALFORMED_REQUEST, // 400
            UNAUTHORIZED, // 401
            ACCESS_DENIED, // 403
            ENDPOINT_NOT_FOUND, // 404
            METHOD_NOT_ALLOWED, // 405
            NOT_ACCEPTABLE, // 406
            RESOURCE_CONFLICT, // 409
            PAYLOAD_TOO_LARGE, // 413
            UNSUPPORTED_MEDIA_TYPE, // 415
            BUSINESS_RULE_VIOLATION, // 422
            TOO_MANY_REQUESTS, // 429
            INTERNAL_ERROR, // 500
            SERVICE_UNAVAILABLE) // 503
            .collect(Collectors.toUnmodifiableMap(
                    e -> e.getHttpStatus().value(), Function.identity()));

    public static ErrorCode porStatus(int status) {
        return POR_STATUS.getOrDefault(status, INTERNAL_ERROR);
    }
}
