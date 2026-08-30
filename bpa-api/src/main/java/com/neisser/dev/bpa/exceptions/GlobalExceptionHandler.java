package com.neisser.dev.bpa.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.warn("Business exception [{}] on {}: {}",
                ex.getErrorCode().getCode(), request.getRequestURI(), ex.getMessage());
        return ProblemDetails.of(ex, request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> errores = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return ProblemDetails.of(
                ErrorCode.CONSTRAINT_VIOLATION,
                "Uno o más parámetros no cumplen las restricciones",
                request.getRequestURI(),
                Map.of("errors", errores, "count", errores.size()));
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception [{}] on {}", ex.getMessage(), request.getRequestURI(), ex);
        return ProblemDetails.of(ErrorCode.INTERNAL_ERROR, "Ha ocurrido un error interno", request.getRequestURI());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        List<String> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toCollection(ArrayList::new));

        ex.getBindingResult().getGlobalErrors()
                .forEach(e -> errores.add(e.getDefaultMessage()));

        ProblemDetail pd = ProblemDetails.of(
                ErrorCode.VALIDATION_ERROR,
                "Los datos enviados no son válidos",
                uriDe(request),
                Map.of("errors", errores, "count", errores.size()));

        return handleExceptionInternal(ex, pd, headers, ErrorCode.VALIDATION_ERROR.getHttpStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        ProblemDetail pd = ProblemDetails.of(
                ErrorCode.MALFORMED_REQUEST,
                "El cuerpo de la petición no se puede leer o está mal formado",
                uriDe(request));

        return handleExceptionInternal(ex, pd, headers, ErrorCode.MALFORMED_REQUEST.getHttpStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            org.springframework.beans.TypeMismatchException ex, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        String detalle = "El valor proporcionado no tiene el tipo esperado";
        if (ex instanceof MethodArgumentTypeMismatchException mate && mate.getRequiredType() != null) {
            detalle = "El parámetro '%s' debe ser de tipo %s"
                    .formatted(mate.getName(), mate.getRequiredType().getSimpleName());
        }

        ProblemDetail pd = ProblemDetails.of(ErrorCode.TYPE_MISMATCH, detalle, uriDe(request));
        return handleExceptionInternal(ex, pd, headers, ErrorCode.TYPE_MISMATCH.getHttpStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        ResponseEntity<Object> respuesta = super.handleExceptionInternal(ex, body, headers, statusCode, request);
        if (respuesta == null) {
            return null;
        }
        if (respuesta.getBody() instanceof ProblemDetail pd) {
            ProblemDetails.completar(pd, respuesta.getStatusCode().value(), uriDe(request));
        }
        return respuesta;
    }

    private static String uriDe(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
