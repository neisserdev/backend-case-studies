package com.neisser.dev.biblioteca.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neisser.dev.biblioteca.dto.EjemplarDTOs.EjemplarResponse;
import com.neisser.dev.biblioteca.service.EjemplarService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EjemplarController {
    private final EjemplarService ejemplarService;

    @PostMapping("/libros/{libroId}/ejemplares")
    public ResponseEntity<EjemplarResponse> crear(@PathVariable Long libroId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ejemplarService.crear(libroId));
    }

    @GetMapping("/libros/{libroId}/ejemplares")
    public ResponseEntity<List<EjemplarResponse>> listarPorLibro(@PathVariable Long libroId) {
        return ResponseEntity.ok().body(ejemplarService.listarPorLibro(libroId));
    }

    @DeleteMapping("/ejemplares/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ejemplarService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
