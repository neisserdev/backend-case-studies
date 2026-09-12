package com.neisser.dev.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.neisser.dev.biblioteca.dto.PrestamoDTOs.PrestamoRequest;
import com.neisser.dev.biblioteca.exceptions.BusinessRuleViolationException;
import com.neisser.dev.biblioteca.mapper.PrestamoMapper;
import com.neisser.dev.biblioteca.model.Ejemplar;
import com.neisser.dev.biblioteca.model.EstadoEjemplar;
import com.neisser.dev.biblioteca.model.Genero;
import com.neisser.dev.biblioteca.model.Libro;
import com.neisser.dev.biblioteca.model.Prestamo;
import com.neisser.dev.biblioteca.model.TipoPrestamo;
import com.neisser.dev.biblioteca.model.TipoUsuario;
import com.neisser.dev.biblioteca.model.Usuario;
import com.neisser.dev.biblioteca.repository.EjemplarRepository;
import com.neisser.dev.biblioteca.repository.PrestamoRepository;
import com.neisser.dev.biblioteca.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private EjemplarRepository ejemplarRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Spy
    private PrestamoMapper prestamoMapper;

    @InjectMocks
    private PrestamoService prestamoService;

    private Libro libroDePrueba() {
        return Libro.builder().id(1L).codigo("PO0001").genero(Genero.POLICIACO).build();
    }

    @Test
    void noSePuedePrestarUnEjemplarConPrestamoAbierto() {
        Libro libro = libroDePrueba();
        Ejemplar ejemplar = Ejemplar.builder()
                .id(1L)
                .libro(libro)
                .estadoEjemplar(EstadoEjemplar.PRESTADO)
                .build();
        Usuario usuario = Usuario.builder().id(1L).tipoUsuario(TipoUsuario.INSCRITO).build();

        when(ejemplarRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(ejemplar));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(BusinessRuleViolationException.class,
                () -> prestamoService.crear(new PrestamoRequest(1L, 1L, TipoPrestamo.UNICO, null)));
    }

    @Test
    void noInscritoNoPuedeSuperarCincoPrestamosAbiertos() {
        Libro libro = libroDePrueba();
        Ejemplar ejemplar = Ejemplar.builder()
                .id(1L)
                .libro(libro)
                .estadoEjemplar(EstadoEjemplar.DISPONIBLE)
                .build();
        Usuario usuario = Usuario.builder().id(1L).tipoUsuario(TipoUsuario.NO_INSCRITO).build();

        when(ejemplarRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(ejemplar));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(prestamoRepository.countByUsuarioIdAndFechaDevolucionRealIsNull(1L)).thenReturn(5L);

        assertThrows(BusinessRuleViolationException.class,
                () -> prestamoService.crear(new PrestamoRequest(1L, 1L, TipoPrestamo.UNICO, null)));
    }

    @Test
    void unLibroNoPuedeSuperarDiezPrestamosEnElMismoMes() {
        Libro libro = libroDePrueba();
        Ejemplar ejemplar = Ejemplar.builder()
                .id(1L)
                .libro(libro)
                .estadoEjemplar(EstadoEjemplar.DISPONIBLE)
                .build();
        Usuario usuario = Usuario.builder().id(1L).tipoUsuario(TipoUsuario.INSCRITO).build();

        when(ejemplarRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(ejemplar));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(prestamoRepository.countByUsuarioIdAndFechaDevolucionRealIsNull(1L)).thenReturn(0L);
        when(prestamoRepository.contarPrestamosDelLibroEnElMes(any(), any(), any())).thenReturn(10L);

        assertThrows(BusinessRuleViolationException.class,
                () -> prestamoService.crear(new PrestamoRequest(1L, 1L, TipoPrestamo.UNICO, null)));
    }

    @Test
    void crearPrestamoMarcaElEjemplarComoPrestado() {
        Libro libro = libroDePrueba();
        Ejemplar ejemplar = Ejemplar.builder()
                .id(1L)
                .libro(libro)
                .estadoEjemplar(EstadoEjemplar.DISPONIBLE)
                .build();
        Usuario usuario = Usuario.builder().id(1L).tipoUsuario(TipoUsuario.INSCRITO).build();

        when(ejemplarRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(ejemplar));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(prestamoRepository.countByUsuarioIdAndFechaDevolucionRealIsNull(1L)).thenReturn(0L);
        when(prestamoRepository.contarPrestamosDelLibroEnElMes(any(), any(), any())).thenReturn(0L);
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(invocation -> {
            Prestamo prestamo = invocation.getArgument(0);
            prestamo.setId(10L);
            return prestamo;
        });

        prestamoService.crear(new PrestamoRequest(1L, 1L, TipoPrestamo.UNICO, null));

        assertEquals(EstadoEjemplar.PRESTADO, ejemplar.getEstadoEjemplar());
    }

    @Test
    void unPrestamoYaDevueltoNoPuedeVolverADevolverse() {
        Prestamo prestamo = Prestamo.builder()
                .id(1L)
                .tipoPrestamo(TipoPrestamo.UNICO)
                .fechaPrestamo(LocalDateTime.now(ZoneOffset.UTC))
                .fechaDevolucionReal(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));

        assertThrows(BusinessRuleViolationException.class,
                () -> prestamoService.devolver(1L));
    }

    @Test
    void unPrestamoUnicoNoPuedeDevolverseOtroDia() {
        Prestamo prestamo = Prestamo.builder()
                .id(1L)
                .tipoPrestamo(TipoPrestamo.UNICO)
                .fechaPrestamo(LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
                .build();
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));

        assertThrows(BusinessRuleViolationException.class,
                () -> prestamoService.devolver(1L));
    }

    @Test
    void devolverUnPrestamoVariosLiberaElEjemplar() {
        Libro libro = libroDePrueba();
        Ejemplar ejemplar = Ejemplar.builder()
                .id(1L)
                .libro(libro)
                .estadoEjemplar(EstadoEjemplar.PRESTADO)
                .build();
        Prestamo prestamo = Prestamo.builder()
                .id(1L)
                .tipoPrestamo(TipoPrestamo.VARIOS)
                .fechaPrestamo(LocalDateTime.now(ZoneOffset.UTC).minusDays(3))
                .fechaDevolucionEsperada(LocalDate.now().plusDays(2))
                .ejemplar(ejemplar)
                .build();
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(ejemplarRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(ejemplar));

        prestamoService.devolver(1L);

        assertEquals(EstadoEjemplar.DISPONIBLE, ejemplar.getEstadoEjemplar());
    }
}
