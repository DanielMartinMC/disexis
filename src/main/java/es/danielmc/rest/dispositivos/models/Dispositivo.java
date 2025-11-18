package es.danielmc.rest.dispositivos.models;

import es.danielmc.rest.titulares.models.Titular;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DISPOSITIVOS")
@Schema(name = "Dispositivo")
public class Dispositivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador de la tarjeta", example = "1")
    private Long id;

    @Column(nullable = false, length = 20)
    @Schema(description = "Nombre de la marca", example = "Apple")
    private String marca;

    @Column(nullable = false, length = 20)
    @Schema(description = "Nombre del modelo", example = "Iphone 15")
    private String modelo;
    
    @Column(nullable = false, length = 100)
    @Schema(description = "Numero del numeroSerie", example = "12341")
    private String numeroSerie;
    
    @Column(nullable = false, length = 32)
    @Schema(description = "Nombre del fabricante", example = "Apple Industries")
    private String fabricante;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Builder.Default
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(unique = true, updatable = false, nullable = false)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "titular_id") // Así se va a llamar en la BD
    private Titular titular;
}