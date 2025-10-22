package es.danielmc.dispositivos.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Dispositivo {

    private final Long id;
    private final String marca;
    private final String modelo;
    private final String numeroSerie;
    private final String fabricante;
    private final String tipo;

    private final LocalDateTime fechaCompra;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final UUID uuid;
}