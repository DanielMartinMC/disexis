package es.danielmc.disexis.titulares.services;

import es.danielmc.titulares.dto.TitularRequestDto;
import es.danielmc.titulares.exceptions.TitularConflictException;
import es.danielmc.titulares.mappers.TitularMapper;
import es.danielmc.titulares.models.Titular;
import es.danielmc.titulares.repositories.TitularesRepository;
import es.danielmc.titulares.services.TitularesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TitularesServiceImplTest {

    private final Titular titular = Titular.builder().id(1L).nombre("Jose").build();
    private final TitularRequestDto titularDto = TitularRequestDto.builder().nombre("Jose").build();

    @Mock
    private TitularesRepository titularesRepository;

    // IMPORTANTE: Añadimos el Spy del mapper para evitar NullPointerException en el servicio
    @Spy
    private TitularMapper titularMapper;

    @InjectMocks
    private TitularesServiceImpl titularesService;

    @Test
    public void testFindAll() {
        when(titularesRepository.findAll()).thenReturn(List.of(titular));

        var res = titularesService.findAll(null);

        assertAll("findAll",
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty())
        );

        verify(titularesRepository, times(1)).findAll();
    }

    @Test
    public void testFindByNombre() {
        when(titularesRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(titular));

        var res = titularesService.findByNombre("Jose");

        assertAll("findByNombre",
                () -> assertNotNull(res),
                () -> assertEquals("Jose", res.getNombre())
        );

        verify(titularesRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
    }

    @Test
    public void testFindById() {
        when(titularesRepository.findById(anyLong())).thenReturn(Optional.of(titular));

        var res = titularesService.findById(1L);

        assertAll("findById",
                () -> assertNotNull(res),
                () -> assertEquals("Jose", res.getNombre())
        );

        verify(titularesRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testSave() {
        when(titularesRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(titularesRepository.save(any(Titular.class))).thenReturn(titular);

        titularesService.save(titularDto);

        assertAll("save",
                () -> assertNotNull(titular),
                () -> assertEquals("Jose", titular.getNombre())
        );

        verify(titularesRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(titularesRepository, times(1)).save(any(Titular.class));
    }

    @Test
    public void testSaveConflict() {
        when(titularesRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(titular));

        var res = assertThrows(TitularConflictException.class,
                () -> titularesService.save(titularDto));

        assertEquals("Ya existe un titular con el nombre Jose", res.getMessage());

        verify(titularesRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(titularesRepository, times(0)).save(any(Titular.class));
    }

    @Test
    public void testUpdate() {
        when(titularesRepository.findById(anyLong())).thenReturn(Optional.of(titular));
        // Para update, simulamos que el nombre no existe o es el mismo titular
        when(titularesRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(titularesRepository.save(any(Titular.class))).thenReturn(titular);

        titularesService.update(1L, titularDto);

        verify(titularesRepository, times(1)).findById(anyLong());
        verify(titularesRepository, times(1)).save(any(Titular.class));
    }

    @Test
    public void testUpdateConflict() {
        Titular otroTitular = Titular.builder().id(2L).nombre("Jose").build();

        when(titularesService.findById(anyLong())).thenReturn(titular); // El titular actual (id 1)
        when(titularesRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(otroTitular)); // Existe otro con ese nombre (id 2)

        var res = assertThrows(TitularConflictException.class,
                () -> titularesService.update(1L, titularDto));

        assertEquals("Ya existe un titular con el nombre Jose", res.getMessage());
        verify(titularesRepository, never()).save(any(Titular.class));
    }

    @Test
    public void testDeleteById() {
        when(titularesRepository.findById(anyLong())).thenReturn(Optional.of(titular));
        when(titularesRepository.existsTarjetaById(anyLong())).thenReturn(false);

        titularesService.deleteById(1L);

        verify(titularesRepository, times(1)).findById(anyLong());
        verify(titularesRepository, times(1)).deleteById(anyLong());
    }
}