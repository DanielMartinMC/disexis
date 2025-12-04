package es.danielmc.titulares.mappers;

import es.danielmc.titulares.dto.TitularRequestDto;
import es.danielmc.titulares.models.Titular;
import org.springframework.stereotype.Component;

@Component
public class TitularMapper {
    public Titular toTitular(TitularRequestDto dto) {
        return Titular.builder()
                .id(null)
                .nombre(dto.getNombre())
                .build();
    }

    public Titular toTitular(TitularRequestDto dto, Titular titular) {
        return Titular.builder()
                .id(titular.getId())
                .nombre(dto.getNombre() != null ? dto.getNombre() : titular.getNombre())
                .createdAt(titular.getCreatedAt())
                //.updatedAt(LocalDateTime.now())
                .isDeleted(dto.getIsDeleted()  != null ? dto.getIsDeleted() : titular.getIsDeleted())
                .build();
    }
}