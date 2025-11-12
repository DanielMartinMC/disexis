package es.danielmc.dispositivos.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class DispositivoResponseDto {
    private  Long id;
    private  String marca;
    private  String modelo;
    private  String numeroSerie;
    private  String fabricante;
    private  String tipo;
    private  LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
    private  UUID uuid;
}