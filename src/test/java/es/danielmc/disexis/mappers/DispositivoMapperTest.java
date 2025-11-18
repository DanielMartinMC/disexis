package es.danielmc.disexis.mappers;

import es.danielmc.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.mappers.DispositivoMapper;
import es.danielmc.dispositivos.models.Dispositivo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DispositivoMapperTest {

    // Inyectamos el mapper
    private final DispositivoMapper dispositivoMapper = new DispositivoMapper();

    @Test
     void toDispositivo_create() {
        // Arrange
        Long id = 1L;
        DispositivoCreateDto dispositivoCreateDto = DispositivoCreateDto.builder()
                .marca("Apple")
                .modelo("iPhone 13")
                .numeroSerie("SN123456789")
                .fabricante("Apple Inc.")
                .tipo("Movil")
                .build();
        // Act
        var res = dispositivoMapper.toDispositivo(id, dispositivoCreateDto);

        // Assert
        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(dispositivoCreateDto.getMarca(), res.getMarca()),
                () -> assertEquals(dispositivoCreateDto.getModelo(), res.getModelo()),
                () -> assertEquals(dispositivoCreateDto.getNumeroSerie(), res.getNumeroSerie()),
                () -> assertEquals(dispositivoCreateDto.getFabricante(), res.getFabricante()),
                () -> assertEquals(dispositivoCreateDto.getTipo(), res.getTipo())
        );
    }

    @Test
    void toDispositivo_update() {
        // Arrange
        Long id = 1L;
        DispositivoUpdateDto dispositivoUpdateDto = DispositivoUpdateDto.builder()
                .marca("Samsung")
                .modelo("Galaxy S21")
                .numeroSerie("SN987654321")
                .fabricante("Samsung Electronics")
                .tipo("Movil")
                .build();

        Dispositivo dispositivo = Dispositivo.builder()
                .id(id)
                .marca(dispositivoUpdateDto.getMarca())
                .modelo(dispositivoUpdateDto.getModelo())
                .numeroSerie(dispositivoUpdateDto.getNumeroSerie())
                .fabricante(dispositivoUpdateDto.getFabricante())
                .tipo(dispositivoUpdateDto.getTipo())
                .build();
        // Act
        var res = dispositivoMapper.toDispositivo(dispositivoUpdateDto, dispositivo);
        // Assert
        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(dispositivoUpdateDto.getMarca(), res.getMarca()),
                () -> assertEquals(dispositivoUpdateDto.getModelo(), res.getModelo()),
                () -> assertEquals(dispositivoUpdateDto.getNumeroSerie(), res.getNumeroSerie()),
                () -> assertEquals(dispositivoUpdateDto.getFabricante(), res.getFabricante()),
                () -> assertEquals(dispositivoUpdateDto.getTipo(), res.getTipo())
        );
    }

    @Test
    void toDispositivoResponseDto() {
        // Arrange
        Dispositivo dispositivo = Dispositivo.builder()
                .id(1L)
                .marca("Samsung")
                .modelo("Galaxy S21")
                .numeroSerie("SN987654321")
                .fabricante("Samsung Electronics")
                .tipo("Movil")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();
        // Act
        var res = dispositivoMapper.toDispositivoResponseDto(dispositivo);
        // Assert
        assertAll(
                () -> assertEquals(dispositivo.getId(), res.getId()),
                () -> assertEquals(dispositivo.getMarca(), res.getMarca()),
                () -> assertEquals(dispositivo.getModelo(), res.getModelo()),
                () -> assertEquals(dispositivo.getNumeroSerie(), res.getNumeroSerie()),
                () -> assertEquals(dispositivo.getFabricante(), res.getFabricante()),
                () -> assertEquals(dispositivo.getTipo(), res.getTipo())
        );
    }
}