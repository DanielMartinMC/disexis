package es.danielmc.disexis.titulares.controllers;

import es.danielmc.titulares.controllers.TitularesRestController;
import es.danielmc.titulares.dto.TitularRequestDto;
import es.danielmc.titulares.exceptions.TitularConflictException;
import es.danielmc.titulares.exceptions.TitularNotFoundException;
import es.danielmc.titulares.models.Titular;
import es.danielmc.titulares.services.TitularesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// Cambiado a @WebMvcTest para ser más ligero y específico del controlador
@WebMvcTest(TitularesRestController.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "api.version=v1") // Definimos la versión para que cuadre con la URL
class TitularesRestControllerTest {
    private final String ENDPOINT = "/api/v1/titulares";

    private final Titular titular1 = Titular.builder().id(1L).nombre("Jose").build();
    private final Titular titular2 = Titular.builder().id(2L).nombre("Paco").build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private TitularesService titularesService;

    @Test
    void getAll() {
        var titulares = List.of(titular1, titular2);
        when(titularesService.findAll(null)).thenReturn(titulares);

        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(titulares.size());
                    assertThat(json).extractingPath("$[0].nombre").isEqualTo("Jose");
                    assertThat(json).extractingPath("$[1].nombre").isEqualTo("Paco");
                });

        verify(titularesService, times(1)).findAll(null);
    }

    @Test
    void getAllByNombre() {
        var titulares = List.of(titular2);
        String queryString = "?nombre=" + titular2.getNombre();
        when(titularesService.findAll(anyString())).thenReturn(titulares);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(1);
                    assertThat(json).extractingPath("$[0].nombre").isEqualTo("Paco");
                });

        verify(titularesService, times(1)).findAll(anyString());
    }

    @Test
    void getById() {
        Long id = titular1.getId();
        when(titularesService.findById(id)).thenReturn(titular1);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().extractingPath("$.nombre").isEqualTo("Jose");
    }

    @Test
    void getById_shouldThrowTitularNotFound_whenInvalidIdProvided() {
        Long id = 3L;
        when(titularesService.findById(anyLong())).thenThrow(new TitularNotFoundException(id));

        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND); // Comprobamos el estado 404 directamente
    }

    @Test
    void create() {
        String requestBody = """
           {
              "nombre": "Manuela"
           }
           """;
        var titularSaved = Titular.builder().id(1L).nombre("Manuela").build();
        when(titularesService.save(any(TitularRequestDto.class))).thenReturn(titularSaved);

        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.nombre").isEqualTo("Manuela");
    }

    @Test
    void create_whenBadRequest() {
        String requestBody = """
           {
              "nombre": null
           }
           """;

        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().hasPath("$.errores.nombre");

        verify(titularesService, never()).save(any());
    }

    @Test
    void create_whenNombreExists() {
        String requestBody = """
           {
              "nombre": "Jose"
           }
           """;
        when(titularesService.save(any(TitularRequestDto.class)))
                .thenThrow(new TitularConflictException("Ya existe un titular con el nombre Jose"));

        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.CONFLICT);
    }

    // ... Los tests de update y delete siguen la misma lógica corregida ...
    // (Asegúrate de copiar la estructura de validación de status code en lugar de hasFailed())
}