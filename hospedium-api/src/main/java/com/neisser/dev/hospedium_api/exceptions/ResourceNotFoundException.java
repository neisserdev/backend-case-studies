package com.neisser.dev.hospedium_api.exceptions;

import java.util.Map;

public class ResourceNotFoundException extends BusinessException {

    private final String recurso;
    private final transient Object id;

    public ResourceNotFoundException(String recurso, Object id) {
        super(ErrorCode.RESOURCE_NOT_FOUND,
                "%s con id %s no encontrado".formatted(recurso, id));
        this.recurso = recurso;
        this.id = id;
    }

    @Override
    public Map<String, Object> getProperties() {
        return Map.of("resource", recurso, "resourceId", String.valueOf(id));
    }
}
