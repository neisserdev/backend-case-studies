package com.neisser.dev.biblioteca.exceptions;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import org.springframework.http.ProblemDetail;

public final class ProblemDetails {

    private static final String BASE_TYPE = "https://neisser.dev/problems/";

    public static final String PROP_TIMESTAMP = "timestamp";
    public static final String PROP_CODE = "code";

    private ProblemDetails() {
    }

    public static URI typeDe(ErrorCode error) {
        return URI.create(BASE_TYPE + error.getCode());
    }

    public static ProblemDetail of(BusinessException ex, String requestURI) {
        return of(ex.getErrorCode(), ex.getMessage(), requestURI, ex.getProperties());
    }

    public static ProblemDetail of(ErrorCode error, String detail, String requestURI) {
        return of(error, detail, requestURI, Map.of());
    }

    public static ProblemDetail of(ErrorCode error, String detail, String requestURI, Map<String, Object> extra) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(error.getHttpStatus(), detail);
        pd.setType(typeDe(error));
        pd.setTitle(error.getTitle());
        pd.setInstance(URI.create(requestURI));
        pd.setProperty(PROP_TIMESTAMP, Instant.now().toString());
        pd.setProperty(PROP_CODE, error.getCode());
        extra.forEach(pd::setProperty);
        return pd;
    }

    public static void completar(ProblemDetail pd, int status, String requestURI) {
        Map<String, Object> props = pd.getProperties();

        if (props == null || !props.containsKey(PROP_CODE)) {
            ErrorCode error = ErrorCode.porStatus(status);
            pd.setProperty(PROP_CODE, error.getCode());
            pd.setType(typeDe(error));
            pd.setTitle(error.getTitle());
        }
        if (props == null || !props.containsKey(PROP_TIMESTAMP)) {
            pd.setProperty(PROP_TIMESTAMP, Instant.now().toString());
        }
        if (pd.getInstance() == null) {
            pd.setInstance(URI.create(requestURI));
        }
    }
}
