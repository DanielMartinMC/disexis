package es.danielmc.dispositivos.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor (force = true)
@Entity
@Table(name = "DISPOSITIVOS")
public class Dispositivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(nullable = false, length = 20)
    private final String marca;
    @Column(nullable = false, length = 25)
    private final String modelo;
    @Column(nullable = false, length = 15)
    private final String numeroSerie;
    @Column(nullable = false, length = 20)
    private final String fabricante;
    @Column(nullable = false, length = 20)
    private final String tipo;
    @Column(nullable = false)
    private final LocalDateTime fechaCompra;

    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private final LocalDateTime createdAt;
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private final LocalDateTime updatedAt;
    @Column(unique = true, updatable = false, nullable = false)
    private final UUID uuid;

    @Column(columnDefinition = "boolean default false")
    private Boolean isDeleted;
}