package es.danielmc.disexis.dispositivos.mappers;

import es.danielmc.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.mappers.DispositivoMapper;
import es.danielmc.dispositivos.models.Dispositivo;
import es.danielmc.titulares.models.Titular;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DispositivoMapperTest {

    private final DispositivoMapper dispositivoMapper = new DispositivoMapper();
    private final Titular titular = Titular.builder().id(1L).nombre("Test").build();

    @Test
    void toDispositivo_create() {
        Long id = 1L;
        DispositivoCreateDto dispositivoCreateDto = DispositivoCreateDto.builder()
                .marca("Apple")
                .modelo("iPhone 13")
                .numeroSerie("SN123456789")
                .fabricante("Apple Inc.")
                .tipo("Movil")
                .titular("Test")
                .build();

        // Nota: El mapper en create ignora el ID pasado y lo pone a null, y usa el titular pasado
        var res = dispositivoMapper.toDispositivo(dispositivoCreateDto, titular);

        assertAll(
                () -> assertEquals(dispositivoCreateDto.getMarca(), res.getMarca()),
                () -> assertEquals(dispositivoCreateDto.getModelo(), res.getModelo()),
                () -> assertEquals(titular, res.getTitular())
        );
    }

    @Test
    void toDispositivo_update() {
        Long id = 1L;
        DispositivoUpdateDto dispositivoUpdateDto = DispositivoUpdateDto.builder()
                .marca("Samsung")
                .modelo("Galaxy S21")
                .build();

        Dispositivo dispositivoOriginal = Dispositivo.builder()
                .id(id)
                .marca("OldBrand")
                .modelo("OldModel")
                .titular(titular)
                .build();

        var res = dispositivoMapper.toDispositivo(dispositivoUpdateDto, dispositivoOriginal);

        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals("Samsung", res.getMarca()),
                () -> assertEquals("Galaxy S21", res.getModelo()),
                () -> assertEquals(titular, res.getTitular())
        );
    }
}