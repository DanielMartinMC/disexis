package es.danielmc.dispositivos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispositivoUpdateDto {
    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede tener más de 50 caracteres")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50, message = "El modelo no puede tener más de 50 caracteres")
    private String modelo;

    @NotBlank(message = "El número de serie es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "El número de serie solo puede contener letras, números y guiones")
    @Size(max = 100, message = "El número de serie no puede tener más de 100 caracteres")
    private String numeroSerie;

    @NotBlank(message = "El fabricante es obligatorio")
    @Size(max = 100, message = "El fabricante no puede tener más de 100 caracteres")
    private String fabricante;

    @NotBlank(message = "El tipo de dispositivo es obligatorio")
    @Size(max = 50, message = "El tipo no puede tener más de 50 caracteres")
    private String tipo;
}

