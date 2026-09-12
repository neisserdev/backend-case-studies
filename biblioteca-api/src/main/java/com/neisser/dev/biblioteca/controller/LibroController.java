package com.neisser.dev.biblioteca.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neisser.dev.biblioteca.dto.LibroDTOs.LibroRequest;
import com.neisser.dev.biblioteca.dto.LibroDTOs.LibroResponse;
import com.neisser.dev.biblioteca.dto.LibroDTOs.LibroUpdateRequest;
import com.neisser.dev.biblioteca.model.Genero;
import com.neisser.dev.biblioteca.service.LibroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/libros")
public class LibroController {
    private final LibroService libroService;

    @PostMapping
    public ResponseEntity<LibroResponse> crear(@Valid @RequestBody LibroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<LibroResponse>> listar() {
        return ResponseEntity.ok().body(libroService.listar());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<LibroResponse>> disponibles(@RequestParam Genero genero) {
        return ResponseEntity.ok().body(libroService.disponiblesPorGenero(genero));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(libroService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LibroResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody LibroUpdateRequest request) {
        return ResponseEntity.ok().body(libroService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
