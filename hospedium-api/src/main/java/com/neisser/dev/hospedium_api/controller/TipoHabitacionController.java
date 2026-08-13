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

import com.neisser.dev.hospedium_api.dto.TipoHabitacionDTOs.TipoHabitacionRequest;
import com.neisser.dev.hospedium_api.dto.TipoHabitacionDTOs.TipoHabitacionResponse;
import com.neisser.dev.hospedium_api.service.TipoHabitacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tipos-habitacion")
public class TipoHabitacionController {
    private final TipoHabitacionService tipoHabitacionService;

    @PostMapping
    public ResponseEntity<TipoHabitacionResponse> crear(@Valid @RequestBody TipoHabitacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoHabitacionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<TipoHabitacionResponse>> crear() {
        return ResponseEntity.ok().body(tipoHabitacionService.mostrarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoHabitacionResponse> crear(@PathVariable Long id) {
        return ResponseEntity.ok().body(tipoHabitacionService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoHabitacionResponse> actualizar(@Valid @RequestBody TipoHabitacionRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok().body(tipoHabitacionService.actualizar(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id) {
        tipoHabitacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
