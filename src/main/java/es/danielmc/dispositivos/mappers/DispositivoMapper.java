package es.danielmc.dispositivos.mappers;

import es.danielmc.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.models.Dispositivo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class DispositivoMapper {
    public Dispositivo toDispositivo(Long id, DispositivoCreateDto dto) {
        return new Dispositivo(
                id,
                dto.getMarca(),
                dto.getModelo(),
                dto.getNumeroSerie(),
                dto.getFabricante(),
                dto.getTipo(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID()
        );
    }

    public Dispositivo toDispositivo(DispositivoUpdateDto dto, Dispositivo dispositivo) {
        // Creamos el dispositivo actualizado con los campos que nos llegan actualizando el updateAt y si son null no se actualizan y se quedan los anteriores
        return new Dispositivo(
                dispositivo.getId(),
                dto.getMarca() != null ? dto.getMarca() : dispositivo.getMarca(),
                dto.getModelo() != null ? dto.getModelo() : dispositivo.getModelo(),
                dto.getNumeroSerie() != null ? dto.getNumeroSerie() : dispositivo.getNumeroSerie(),
                dto.getFabricante() != null ? dto.getFabricante() : dispositivo.getFabricante(),
                dto.getTipo() != null ? dto.getTipo() : dispositivo.getTipo(),
                null,
                dispositivo.getCreatedAt(),
                LocalDateTime.now(),
                dispositivo.getUuid()
        );
    }

    public DispositivoResponseDto toDispositivoResponseDto(Dispositivo dispositivo) {
        return new DispositivoResponseDto(
                dispositivo.getId(),
                dispositivo.getMarca(),
                dispositivo.getModelo(),
                dispositivo.getNumeroSerie(),
                dispositivo.getFabricante(),
                dispositivo.getTipo(),

                dispositivo.getCreatedAt(),
                dispositivo.getUpdatedAt(),
                dispositivo.getUuid()
        );
    }
    public List<DispositivoResponseDto> toDispositivoResponseDto(List<Dispositivo> dispositivos) {
        return dispositivos.stream()
                .map(this::toDispositivoResponseDto)
                .toList();
    }
}