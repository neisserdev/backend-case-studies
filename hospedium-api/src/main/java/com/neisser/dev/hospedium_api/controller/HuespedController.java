package com.neisser.dev.hospedium_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neisser.dev.hospedium_api.dto.HuespedDTOs.HuespedRequest;
import com.neisser.dev.hospedium_api.dto.HuespedDTOs.HuespedResponse;
import com.neisser.dev.hospedium_api.service.HuespedService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/huespedes")
public class HuespedController {
    private final HuespedService huespedService;

    @PostMapping
    public ResponseEntity<HuespedResponse> crear(@Valid @RequestBody HuespedRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(huespedService.crear(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HuespedResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(huespedService.buscarPorId(id));
    }
}
