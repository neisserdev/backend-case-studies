package com.neisser.dev.hospedium_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neisser.dev.hospedium_api.dto.AsignacionDTOs.AsignacionRequest;
import com.neisser.dev.hospedium_api.dto.AsignacionDTOs.AsignacionResponse;
import com.neisser.dev.hospedium_api.dto.DisponibilidadRequest;
import com.neisser.dev.hospedium_api.dto.HabitacionDTOs.HabitacionResponse;
import com.neisser.dev.hospedium_api.dto.ReservaDTOs.ReservaRequest;
import com.neisser.dev.hospedium_api.dto.ReservaDTOs.ReservaResponse;
import com.neisser.dev.hospedium_api.service.ReservaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReservaController {
    private final ReservaService reservaService;

    @PostMapping("/reservas")
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(request));
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<ReservaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(reservaService.buscarPorId(id));
    }

    @PatchMapping("/reservas/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok().body(reservaService.cancelar(id));
    }

    @PostMapping("/habitaciones/disponibles")
    public ResponseEntity<List<HabitacionResponse>> buscarDisponibles(
            @Valid @RequestBody DisponibilidadRequest request) {
        return ResponseEntity.ok().body(reservaService.buscarDisponibles(request));
    }

    @PostMapping("/reservas/{id}/asignar")
    public ResponseEntity<AsignacionResponse> asignar(
            @PathVariable Long id,
            @Valid @RequestBody AsignacionRequest request) {
        return ResponseEntity.ok().body(reservaService.asignar(id, request));
    }

    @GetMapping("/huespedes/{id}/reservas")
    public ResponseEntity<List<ReservaResponse>> historial(@PathVariable Long id) {
        return ResponseEntity.ok().body(reservaService.historial(id));
    }
}