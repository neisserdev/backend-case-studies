package com.neisser.dev.bpa.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.neisser.dev.bpa.dto.ClienteDTOs.ClienteRequest;
import com.neisser.dev.bpa.dto.ClienteDTOs.ClienteUpdateRequest;
import com.neisser.dev.bpa.dto.CuentaDTOs.CuentaRequest;
import com.neisser.dev.bpa.dto.OperacionDTOs.OperacionRequest;
import com.neisser.dev.bpa.model.ClasificacionEmpresa;
import com.neisser.dev.bpa.model.TipoCliente;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void configurarValidador() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        factory.close();
    }

    @Test
    void empresaRequiereClasificacion() {
        ClienteRequest request = new ClienteRequest("Empresa", "EMP-1", TipoCliente.EMPRESA, null);
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void particularNoAdmiteClasificacionEmpresarial() {
        ClienteRequest request = new ClienteRequest(
                "Persona", "PER-1", TipoCliente.PARTICULAR, ClasificacionEmpresa.RENTABLE);
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void actualizacionParcialRechazaTextosVaciosPeroAdmiteCamposOmitidos() {
        assertFalse(validator.validate(new ClienteUpdateRequest("", "   ", null)).isEmpty());
        assertTrue(validator.validate(new ClienteUpdateRequest("Nombre válido", null, null)).isEmpty());
    }

    @Test
    void montosMonetariosAdmitenComoMaximoDosDecimales() {
        assertFalse(validator.validate(new CuentaRequest(1L, new BigDecimal("50.009"))).isEmpty());
        assertFalse(validator.validate(new OperacionRequest(new BigDecimal("1.009"))).isEmpty());
        assertTrue(validator.validate(new OperacionRequest(new BigDecimal("1.00"))).isEmpty());
    }
}
