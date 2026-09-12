package com.neisser.dev.biblioteca.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "prestamo")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prestamo_id")
    private Long id;

    @Column(name = "tipo_prestamo", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TipoPrestamo tipoPrestamo;

    @Column(name = "fecha_prestamo", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaPrestamo = LocalDateTime.now(ZoneOffset.UTC);

    @Column(name = "fecha_devolucion_esperada", updatable = false)
    private LocalDate fechaDevolucionEsperada;

    @Column(name = "fecha_devolucion_real")
    private LocalDateTime fechaDevolucionReal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejemplar_id", nullable = false, updatable = false)
    private Ejemplar ejemplar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;
}
