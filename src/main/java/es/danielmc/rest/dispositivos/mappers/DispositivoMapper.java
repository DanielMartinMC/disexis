package es.danielmc.rest.dispositivos.mappers;

import es.danielmc.rest.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.rest.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.rest.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.rest.dispositivos.models.Dispositivo;
import es.danielmc.rest.titulares.models.Titular;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// FIX: Se eliminan las anotaciones redundantes de Lombok para el constructor.
@Component
public class DispositivoMapper {
    public Dispositivo toDispositivo(DispositivoCreateDto dispositivoCreateDto, Titular titular) {
        return Dispositivo.builder()
                .id(null)
                .marca(dispositivoCreateDto.getMarca())
                .modelo(dispositivoCreateDto.getModelo())
                .numeroSerie(dispositivoCreateDto.getNumeroSerie())
                .fabricante(dispositivoCreateDto.getFabricante())
                .tipo(dispositivoCreateDto.getTipo())
                .titular(titular)
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


    }

    public Dispositivo toDispositivo(DispositivoUpdateDto dispositivoUpdateDto, Dispositivo dispositivo) {
        // Creamos el dispositivo actualizado con los campos que nos llegan actualizando el updateAt y si son null no se actualizan y se quedan los anteriores
        return Dispositivo.builder()
                .id(dispositivo.getId())
                .marca(dispositivoUpdateDto.getMarca() != null ? dispositivoUpdateDto.getMarca() : dispositivo.getMarca())
                .modelo(dispositivoUpdateDto.getModelo() != null ? dispositivoUpdateDto.getModelo() : dispositivo.getModelo())
                .numeroSerie(dispositivoUpdateDto.getNumeroSerie() != null ? dispositivoUpdateDto.getNumeroSerie() : dispositivo.getNumeroSerie())
                .fabricante(dispositivoUpdateDto.getFabricante() != null ? dispositivoUpdateDto.getFabricante() : dispositivo.getFabricante())
                .tipo(dispositivoUpdateDto.getTipo() != null ? dispositivoUpdateDto.getTipo() : dispositivo.getTipo()) // FIX: Añadida la lógica para el campo 'tipo'
                .titular(dispositivo.getTitular())
                .createdAt(dispositivo.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .uuid(dispositivo.getUuid())
                .build();

    }

    public DispositivoResponseDto toDispositivoResponseDto(Dispositivo dispositivo) {
        return DispositivoResponseDto.builder()
                .id(dispositivo.getId())
                .marca(dispositivo.getMarca())
                .modelo(dispositivo.getModelo())
                .numeroSerie(dispositivo.getNumeroSerie())
                .fabricante(dispositivo.getFabricante())
                .tipo(dispositivo.getTipo())
                .titular(dispositivo.getTitular().getNombre())
                .createdAt(dispositivo.getCreatedAt())
                .updatedAt(dispositivo.getUpdatedAt())
                .uuid(dispositivo.getUuid())
                .build();
    }
    public List<DispositivoResponseDto> toDispositivoResponseDto(List<Dispositivo> dispositivos) {
        return dispositivos.stream()
                .map(this::toDispositivoResponseDto)
                .toList();
    }
    public Page<DispositivoResponseDto> toResponseDtoPage(Page<Dispositivo> dispositivos) {
        return dispositivos.map(this::toDispositivoResponseDto);
    }
}