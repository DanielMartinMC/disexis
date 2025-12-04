package es.danielmc.titulares.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.danielmc.dispositivos.models.Dispositivo;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class TitularRequestDto {
    private Long id;
    private String nombre;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private Boolean isDeleted = false;
}