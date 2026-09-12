package com.neisser.dev.biblioteca.controller;

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

import com.neisser.dev.biblioteca.dto.PrestamoDTOs.PrestamoRequest;
import com.neisser.dev.biblioteca.dto.PrestamoDTOs.PrestamoResponse;
import com.neisser.dev.biblioteca.service.PrestamoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PrestamoController {
    private final PrestamoService prestamoService;

    @PostMapping("/prestamos")
    public ResponseEntity<PrestamoResponse> crear(@Valid @RequestBody PrestamoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.crear(request));
    }

    @GetMapping("/prestamos/{id}")
    public ResponseEntity<PrestamoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(prestamoService.buscarPorId(id));
    }

    @PatchMapping("/prestamos/{id}/devolver")
    public ResponseEntity<PrestamoResponse> devolver(@PathVariable Long id) {
        return ResponseEntity.ok().body(prestamoService.devolver(id));
    }

    @GetMapping("/usuarios/{usuarioId}/prestamos")
    public ResponseEntity<List<PrestamoResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok().body(prestamoService.listarPorUsuario(usuarioId));
    }
}
