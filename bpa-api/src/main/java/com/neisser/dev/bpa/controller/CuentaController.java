package com.neisser.dev.bpa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neisser.dev.bpa.dto.CuentaDTOs.CuentaRequest;
import com.neisser.dev.bpa.dto.CuentaDTOs.CuentaResponse;
import com.neisser.dev.bpa.dto.OperacionDTOs.OperacionRequest;
import com.neisser.dev.bpa.dto.OperacionDTOs.OperacionResponse;
import com.neisser.dev.bpa.service.CuentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cuentas")
public class CuentaController {
    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crear(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(cuentaService.buscarPorId(id));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<CuentaResponse> cerrar(@PathVariable Long id) {
        return ResponseEntity.ok().body(cuentaService.cerrar(id));
    }

    @PostMapping("/{id}/depositos")
    public ResponseEntity<OperacionResponse> depositar(
            @PathVariable Long id,
            @Valid @RequestBody OperacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.depositar(id, request));
    }

    @PostMapping("/{id}/extracciones")
    public ResponseEntity<OperacionResponse> extraer(
            @PathVariable Long id,
            @Valid @RequestBody OperacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.extraer(id, request));
    }

    @GetMapping("/{id}/operaciones")
    public ResponseEntity<List<OperacionResponse>> operaciones(@PathVariable Long id) {
        return ResponseEntity.ok().body(cuentaService.historial(id));
    }

    @PostMapping("/interes/anual")
    public ResponseEntity<List<OperacionResponse>> aplicarInteresAnual() {
        return ResponseEntity.ok().body(cuentaService.aplicarInteresAnual());
    }
}
