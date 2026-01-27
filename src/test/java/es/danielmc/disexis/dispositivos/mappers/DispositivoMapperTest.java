package es.danielmc.disexis.dispositivos.mappers;

import es.danielmc.rest.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.rest.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.rest.dispositivos.mappers.DispositivoMapper;
import es.danielmc.rest.dispositivos.models.Dispositivo;
import es.danielmc.rest.titulares.models.Titular;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DispositivoMapperTest {

    private final Titular titular = Titular.builder().id(1L).nombre("Pepe").build();

    // Inyectamos el mapper
    private final DispositivoMapper dispositivoMapper = new DispositivoMapper();

    @Test
    void toDispositivo_create() {
        // Arrange
        DispositivoCreateDto dispositivoCreateDto = DispositivoCreateDto.builder()
                .marca("Samsung")
                .modelo("Galaxy S21")
                .numeroSerie("SN123456789")
                .fabricante("Samsung Electronics")
                .tipo("Movil")
                .titular("Pepe")
                .build();

        // Act
        var res = dispositivoMapper.toDispositivo(dispositivoCreateDto, titular);

        // Assert
        assertAll(
                () -> assertEquals(dispositivoCreateDto.getMarca(), res.getMarca()),
                () -> assertEquals(dispositivoCreateDto.getModelo(), res.getModelo()),
                () -> assertEquals(dispositivoCreateDto.getNumeroSerie(), res.getNumeroSerie()),
                () -> assertEquals(dispositivoCreateDto.getFabricante(), res.getFabricante()),
                () -> assertEquals(dispositivoCreateDto.getTipo(), res.getTipo()),
                () -> assertEquals(titular, res.getTitular()) // Comprobamos que asigna el objeto titular
        );
    }

    @Test
    void toDispositivo_update() {
        // Arrange
        Long id = 1L;
        DispositivoUpdateDto dispositivoUpdateDto = DispositivoUpdateDto.builder()
                .marca("Apple")
                .modelo("iPhone 13")
                .numeroSerie("SN987654321")
                .fabricante("Apple Inc.")
                .tipo("Movil")
                .titular("Pepe")
                .build();

        Dispositivo dispositivoOriginal = Dispositivo.builder()
                .id(id)
                .marca("Samsung")
                .modelo("Galaxy S21")
                .numeroSerie("SN123456789")
                .fabricante("Samsung Electronics")
                .tipo("Movil")
                .titular(titular)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        // Act
        var res = dispositivoMapper.toDispositivo(dispositivoUpdateDto, dispositivoOriginal);

        // Assert
        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(dispositivoUpdateDto.getMarca(), res.getMarca()),
                () -> assertEquals(dispositivoUpdateDto.getModelo(), res.getModelo()),
                () -> assertEquals(dispositivoUpdateDto.getNumeroSerie(), res.getNumeroSerie()),
                () -> assertEquals(dispositivoUpdateDto.getFabricante(), res.getFabricante()),
                () -> assertEquals(dispositivoUpdateDto.getTipo(), res.getTipo()),
                // El update suele mantener el created_at original
                () -> assertEquals(dispositivoOriginal.getCreatedAt(), res.getCreatedAt())
        );
    }

    @Test
    void toDispositivoResponseDto() {
        // Arrange
        Dispositivo dispositivo = Dispositivo.builder()
                .id(1L)
                .marca("Samsung")
                .modelo("Galaxy S21")
                .numeroSerie("SN123456789")
                .fabricante("Samsung Electronics")
                .tipo("Movil")
                .titular(titular)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
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
                () -> assertEquals(dispositivo.getTipo(), res.getTipo()),
                () -> assertEquals(dispositivo.getTitular().getNombre(), res.getTitular()), // En response devolvemos el nombre String
                () -> assertEquals(dispositivo.getUuid(), res.getUuid()),
                () -> assertEquals(dispositivo.getCreatedAt(), res.getCreatedAt()),
                () -> assertEquals(dispositivo.getUpdatedAt(), res.getUpdatedAt())
        );
    }
}
