package com.neisser.dev.bpa.validation;

import com.neisser.dev.bpa.dto.ClienteDTOs.ClienteRequest;
import com.neisser.dev.bpa.model.TipoCliente;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ClasificacionEmpresaRequeridaValidator
        implements ConstraintValidator<ClasificacionEmpresaRequerida, ClienteRequest> {

    @Override
    public boolean isValid(ClienteRequest request, ConstraintValidatorContext context) {
        if (request == null || request.tipoCliente() != TipoCliente.EMPRESA) {
            return true;
        }

        return request.clasificacionEmpresa() != null;
    }
}
