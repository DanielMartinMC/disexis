package es.danielmc.disexis.titulares.mappers;


import es.danielmc.rest.titulares.mappers.TitularRequestDto;
import es.danielmc.rest.titulares.models.Titular;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TitularesMapperTest {
    private final Titular titular = Titular.builder().id(1L).nombre("Jose").build();

    // Inyectamos el mapper
    private final TitularRequestDto titularesMapper = new TitularRequestDto();

    private final es.danielmc.rest.titulares.dto.TitularRequestDto titularDto = es.danielmc.rest.titulares.dto.TitularRequestDto.builder().nombre("JOSE").build();

    @Test
    public void whenToTitular_thenReturnTitular() {
        Titular mappedTitular = titularesMapper.toTitular(titularDto);

        assertAll("whenToTitular_thenReturnTitular",
                () -> assertEquals(titularDto.getNombre(), mappedTitular.getNombre())
        );
    }

    @Test
    public void whenToTitularWithExistingTitular_thenReturnUpdatedTitular() {

        Titular updatedTitular = titularesMapper.toTitular(titularDto, titular);

        assertAll("whenToTitularWithExistingTitular_thenReturnUpdatedTitular",
                () -> assertEquals(titularDto.getNombre(), updatedTitular.getNombre())
        );
    }
}