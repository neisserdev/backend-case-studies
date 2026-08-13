package com.neisser.dev.hospedium_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neisser.dev.hospedium_api.dto.HabitacionDTOs.HabitacionRequest;
import com.neisser.dev.hospedium_api.dto.HabitacionDTOs.HabitacionResponse;
import com.neisser.dev.hospedium_api.service.HabitacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/habitaciones")
public class HabitacionController {
    private final HabitacionService habitacionService;

    @PostMapping
    public ResponseEntity<HabitacionResponse> crear(@Valid @RequestBody HabitacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitacionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<HabitacionResponse>> crear() {
        return ResponseEntity.ok().body(habitacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionResponse> crear(@PathVariable Long id) {
        return ResponseEntity.ok().body(habitacionService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitacionResponse> actualizar(@PathVariable Long id,
            @Valid @RequestBody HabitacionRequest request) {
        return ResponseEntity.ok().body(habitacionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id) {
        habitacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}