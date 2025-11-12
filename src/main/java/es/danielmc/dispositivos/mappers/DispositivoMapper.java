package es.danielmc.dispositivos.mappers;

import es.danielmc.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.models.Dispositivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DispositivoMapper {
    public Dispositivo toDispositivo(Long id, DispositivoCreateDto dispositivoCreateDto) {
        return Dispositivo.builder()
                .id(id)
                .marca(dispositivoCreateDto.getMarca())
                .modelo(dispositivoCreateDto.getModelo())
                .numeroSerie(dispositivoCreateDto.getNumeroSerie())
                .fabricante(dispositivoCreateDto.getFabricante())
                .tipo(dispositivoCreateDto.getTipo())
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
}