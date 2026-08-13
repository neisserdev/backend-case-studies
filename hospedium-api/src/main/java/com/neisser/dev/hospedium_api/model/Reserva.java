package com.neisser.dev.hospedium_api.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserva_id")
    private Long id;

    @Column(name = "fechaEntrada")
    private LocalDate fechaEntrada;

    @Column(name = "fechaSalida")
    private LocalDate fechaSalida;

    @Column(name = "estado_reserva")
    @Builder.Default
    private EstadoReserva estadoReserva = EstadoReserva.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "huesped_id", nullable = false)
    private Huesped huesped;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion;
}