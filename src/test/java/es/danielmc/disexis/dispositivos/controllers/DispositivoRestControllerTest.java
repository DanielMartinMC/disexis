package es.danielmc.disexis.dispositivos.controllers;

import es.danielmc.rest.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.rest.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.rest.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.rest.dispositivos.exceptions.DispositivoNotFound;
import es.danielmc.rest.dispositivos.services.DispositivosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class DispositivoRestControllerTest {

    private final String ENDPOINT = "/api/v1/dispositivos";

    private final DispositivoResponseDto dispositivoResponse1 = DispositivoResponseDto.builder()
            .id(1L)
            .marca("Samsung")
            .modelo("Galaxy S21")
            .numeroSerie("SN123456789")
            .fabricante("Samsung Electronics")
            .tipo("Movil")
            .titular("Daniel")
            .uuid(UUID.randomUUID())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final DispositivoResponseDto dispositivoResponse2 = DispositivoResponseDto.builder()
            .id(2L)
            .marca("Apple")
            .modelo("iPhone 13")
            .numeroSerie("SN987654321")
            .fabricante("Apple Inc.")
            .tipo("Movil")
            .titular("Cristina")
            .uuid(UUID.randomUUID())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private DispositivosService dispositivosService;

    @Test
    void getAll() {

        var dispositivoResponses = List.of(dispositivoResponse1, dispositivoResponse2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(dispositivoResponses);
        when(dispositivosService.findAll(Optional.empty(), Optional.empty(),Optional.empty(), pageable))
                .thenReturn(page);

        // Act. Consultar el endpoint
        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(dispositivoResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse1);
                    assertThat(json).extractingPath("$[1]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse2);
                });

        // Verify
        verify(dispositivosService, times(1))
                .findAll(Optional.empty(), Optional.empty(),Optional.empty(), pageable);
    }

    @Test
    void getAllByMarca() {
        // Arrange
        var dispositivoResponses = List.of(dispositivoResponse2);
        String queryString = "?marca=" + dispositivoResponse2.getMarca();
        Optional<String> marca = Optional.of(dispositivoResponse2.getMarca());
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(dispositivoResponses);
        // Configuramos el mock: marca="Samsung", titular=null
        when(dispositivosService.findAll(marca, Optional.empty(), Optional.empty(), pageable))
                .thenReturn(page);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(dispositivoResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse1);
                });

        // Verify
        verify(dispositivosService, times(1))
                .findAll(marca, Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllByTitular() {
        // Arrange
        var dispositivoResponses = List.of(dispositivoResponse2);
        String queryString = "?titular=" + dispositivoResponse2.getTitular();
        Optional<String> titular = Optional.of(dispositivoResponse2.getTitular());
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(dispositivoResponses);
        when(dispositivosService.findAll(Optional.empty(), titular, Optional.empty(), pageable))
                .thenReturn(page);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(dispositivoResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse2);
                });

        // Verify
        verify(dispositivosService, only())
                .findAll(Optional.empty(), titular, Optional.empty(), pageable);
    }

    @Test
    void getAllByMarcaAndTitular() {
        // Arrange
        var dispositivoResponses = List.of(dispositivoResponse2);
        String queryString = "?marca=" + dispositivoResponse2.getMarca() + "&"
                + "titular=" + dispositivoResponse2.getTitular();
        Optional<String> marca = Optional.of(dispositivoResponse2.getMarca());
        Optional<String> titular = Optional.of(dispositivoResponse2.getTitular());
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(dispositivoResponses);
        when(dispositivosService.findAll(marca, titular, Optional.empty(), pageable)).thenReturn(page);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(dispositivoResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse2);
                });

        // Verify
        verify(dispositivosService, only()).findAll(marca, titular, Optional.empty(), pageable);
    }

    @Test
    void getById_shouldReturnJsonWithDispositivo_whenValidIdProvided() {
        // Arrange
        Long id = dispositivoResponse1.getId();
        when(dispositivosService.findById(id)).thenReturn(dispositivoResponse1);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(DispositivoResponseDto.class)
                .isEqualTo(dispositivoResponse1);

        // Verify
        verify(dispositivosService, only()).findById(anyLong());
    }

    @Test
    void getById_shouldThrowDispositivoNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 99L;
        when(dispositivosService.findById(anyLong())).thenThrow(new DispositivoNotFound(id));

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus4xxClientError() // 404
                .hasFailed().failure()
                .isInstanceOf(DispositivoNotFound.class)
                .hasMessageContaining("no encontrado");

        // Verify
        verify(dispositivosService, only()).findById(anyLong());
    }

    @Test
    void create() {
        // Arrange
        String requestBody = """
               {
                  "marca": "Xiaomi",
                  "modelo": "Redmi Note",
                  "numeroSerie": "SN-NEW-123",
                  "fabricante": "Xiaomi Corp",
                  "tipo": "Movil",
                  "titular": "NuevoUsuario"
               }
               """;

        var dispositivoSaved = DispositivoResponseDto.builder()
                .id(3L)
                .marca("Xiaomi")
                .modelo("Redmi Note")
                .numeroSerie("SN-NEW-123")
                .fabricante("Xiaomi Corp")
                .tipo("Movil")
                .titular("NuevoUsuario")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        when(dispositivosService.save(any(DispositivoCreateDto.class))).thenReturn(dispositivoSaved);

        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DispositivoResponseDto.class)
                .isEqualTo(dispositivoSaved);

        verify(dispositivosService, only()).save(any(DispositivoCreateDto.class));
    }

    @Test
    void create_whenBadRequest() {
        // Arrange: Enviamos datos inválidos (campos obligatorios vacíos o nulos)
        String requestBody = """
               {
                  "marca": "",
                  "modelo": null,
                  "numeroSerie": "!!!CaracteresInvalidos!!!",
                  "fabricante": "",
                  "tipo": "",
                  "titular": ""
               }
               """;

        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errors", path -> {
                    // Verificamos que los campos fallan la validación
                    // Nota: El path "$.errors" depende de cómo mapees ProblemDetail en tu controlador
                    // En tu código actual de DispositivosRestController usas 'problemDetail.setProperty("errors", ...)'
                    assertThat(path).hasFieldOrProperty("marca");
                    assertThat(path).hasFieldOrProperty("modelo");
                    assertThat(path).hasFieldOrProperty("numeroSerie");
                    assertThat(path).hasFieldOrProperty("fabricante");
                });

        verify(dispositivosService, never()).save(any(DispositivoCreateDto.class));
    }

    @Test
    void update() {
        // Arrange
        Long id = 1L;
        String requestBody = """
               {
                  "marca": "Samsung Updated",
                  "modelo": "S21 Ultra",
                  "numeroSerie": "SN-UPDATED",
                  "fabricante": "Samsung",
                  "tipo": "Movil",
                  "titular": "Daniel"
               }
               """;

        var dispositivoUpdated = DispositivoResponseDto.builder()
                .id(1L)
                .marca("Samsung Updated")
                .modelo("S21 Ultra")
                .numeroSerie("SN-UPDATED")
                .fabricante("Samsung")
                .tipo("Movil")
                .titular("Daniel")
                .build();

        when(dispositivosService.update(eq(id), any(DispositivoUpdateDto.class))).thenReturn(dispositivoUpdated);

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(DispositivoResponseDto.class)
                .isEqualTo(dispositivoUpdated);

        verify(dispositivosService, only()).update(eq(id), any(DispositivoUpdateDto.class));
    }

    @Test
    void update_shouldThrowDispositivoNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 99L;
        String requestBody = """
               {
                  "marca": "Test",
                  "modelo": "Test",
                  "numeroSerie": "123",
                  "fabricante": "Test",
                  "tipo": "Test",
                  "titular": "Test"
               }
               """;

        when(dispositivosService.update(eq(id), any(DispositivoUpdateDto.class)))
                .thenThrow(new DispositivoNotFound(id));

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(DispositivoNotFound.class)
                .hasMessageContaining("no encontrado");

        verify(dispositivosService, only()).update(eq(id), any());
    }

    @Test
    void updatePartial() {
        // Arrange
        Long id = 1L;
        // En PATCH solemos mandar solo lo que cambia, pero DTO validation puede requerir campos
        // Depende de tu implementación de UpdateDto. En tu caso los campos tienen @NotBlank,
        // así que si el DTO de update valida todo, hay que enviar todo.
        String requestBody = """
               {
                  "marca": "Marca Nueva",
                  "modelo": "Modelo Nuevo",
                  "numeroSerie": "SN-123",
                  "fabricante": "Fab",
                  "tipo": "Tipo",
                  "titular": "Titular"
               }
               """;

        var dispositivoUpdated = DispositivoResponseDto.builder()
                .id(1L)
                .marca("Marca Nueva")
                .build();

        when(dispositivosService.update(eq(id), any(DispositivoUpdateDto.class))).thenReturn(dispositivoUpdated);

        // Act
        var result = mockMvcTester.patch()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(DispositivoResponseDto.class)
                .isEqualTo(dispositivoUpdated);

        verify(dispositivosService, only()).update(eq(id), any(DispositivoUpdateDto.class));
    }

    @Test
    void delete() {
        // Arrange
        Long id = 1L;
        doNothing().when(dispositivosService).deleteById(anyLong());

        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(dispositivosService, only()).deleteById(anyLong());
    }

    @Test
    void delete_shouldThrowDispositivoNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 99L;
        doThrow(new DispositivoNotFound(id)).when(dispositivosService).deleteById(anyLong());

        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(DispositivoNotFound.class)
                .hasMessageContaining("no encontrado");

        verify(dispositivosService, only()).deleteById(anyLong());
    }
}