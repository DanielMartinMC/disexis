package es.danielmc.disexis.controllers;

import es.danielmc.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.exeptions.DispositivoNotFound;
import es.danielmc.dispositivos.services.DispositivosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import  static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DispositivoRestControllerTest {

    private final String ENDPOINT = "/api/v1/dispositivos";

    private final DispositivoResponseDto dispositivoResponse1 = DispositivoResponseDto.builder()
            .id(1L)
            .marca("Marca1")
            .modelo("Modelo1")
            .numeroSerie("1234567890")
            .fabricante("Fabricante1")
            .tipo("Tipo1")
            .build();
    private final DispositivoResponseDto dispositivoResponse2 = DispositivoResponseDto.builder()
            .id(2L)
            .marca("Marca2")
            .modelo("Modelo2")
            .numeroSerie("0987654321")
            .fabricante("Fabricante2")
            .tipo("Tipo2")
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private DispositivosService dispositivosService;

    @Test
    void getAll() {
        var dispositivoResponses = List.of(dispositivoResponse1, dispositivoResponse2);
        when(dispositivosService.findAll(null, null)).thenReturn(dispositivoResponses);

        var result = mockMvcTester .get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(dispositivoResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse1);
                    assertThat(json).extractingPath("$[1]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse2);
                });

        verify(dispositivosService, times(1)).findAll(null, null);

    }

    @Test
    void getAllByMarca() {
        var DispositivosResponses = List.of(dispositivoResponse2);
        String queryString = "?marca=" + dispositivoResponse2.getMarca();
        when(dispositivosService.findAll(anyString(), isNull())).thenReturn(DispositivosResponses);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(DispositivosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse2);
                });
        verify(dispositivosService, times(1)).findAll(anyString(), isNull());
    }
    @Test
    void getAllByModelo() {
        var DispositivosResponses = List.of(dispositivoResponse2);
        String queryString = "?modelo=" + dispositivoResponse2.getModelo();
        when(dispositivosService.findAll(isNull(), anyString())).thenReturn(DispositivosResponses);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(DispositivosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse2);
                });

        verify(dispositivosService, times(1)).findAll(isNull(), anyString());
    }

    @Test
    void getAllByMarcaAndModelo() {
        var DispositivosResponses = List.of(dispositivoResponse2);
        String queryString = "?marca=" + dispositivoResponse2.getMarca() + "&modelo=" + dispositivoResponse2.getModelo();
        when(dispositivosService.findAll(anyString(), anyString())).thenReturn(DispositivosResponses);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(DispositivosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse2);
                });

        verify(dispositivosService,only()).findAll(anyString(), anyString());
    }

    @Test
    void getById_shouldReturnJsonWithDispositivo_whenValidIdProvided() {
        // Arrange
        Long id = dispositivoResponse1.getId();
        when(dispositivosService.findById(id)).thenReturn(dispositivoResponse1);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id.toString())
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
        Long id = 3L;
        when(dispositivosService.findById(anyLong())).thenThrow(new DispositivoNotFound(id));

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatus4xxClientError()
                // throws DispositivoNotFoundException
                .hasFailed().failure()
                .isInstanceOf(DispositivoNotFound.class)
                .hasMessageContaining("no encontrada");

        // Verify
        verify(dispositivosService, only()).findById(anyLong());

    }

    @Test
    void create() {
        // Arrange
        String requestBody = """
           {
              "marca": "Samsung",
              "modelo": "Galaxy S21",
              "numeroSerie": "123456789012345",
              "fabricante": "Samsung Electronics",
              "tipo": "Smartphone"
           }
           """;


        var dispositivoSaved = DispositivoResponseDto.builder()
                .id(1L)
                .marca("Samsung")
                .modelo("Galaxy S21")
                .numeroSerie("123456789012345")
                .fabricante("Samsung Electronics")
                .tipo("Smartphone")
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
        // Arrange
        String requestBody = """
           {
              marca: "",
              modelo: "",
              numeroSerie: "",
              fabricante: "",
              tipo: ""
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
                .hasPathSatisfying("$.errores", path -> {
                    assertThat(path).hasFieldOrProperty("cvc");
                    assertThat(path).hasFieldOrProperty("numero");
                    assertThat(path).hasFieldOrProperty("fechaCaducidad");
                });

        verify(dispositivosService, never()).save(any(DispositivoCreateDto.class));

    }

    @Test
    void update() {
        // Arrange
        Long id = 1L;
        String requestBody = """
           {
              "numeroSerie": 211213121312
           }
           """;

        var dispositivoSaved = DispositivoResponseDto.builder()
                .id(1L)
                .marca("Marca1")
                .modelo("Modelo1")
                .numeroSerie("211213121312")
                .fabricante("Fabricante1")
                .tipo("Tipo1")
                .build();

        when(dispositivosService.update(anyLong(), any(DispositivoUpdateDto.class))).thenReturn(dispositivoSaved);

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT+ "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(DispositivoResponseDto.class)
                .isEqualTo(dispositivoSaved);

        verify(dispositivosService, only()).update(anyLong(), any(DispositivoUpdateDto.class));

    }

    @Test
    void update_shouldThrowDispositivoNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 3L;
        String requestBody = """
           {
              "numeroSerie": 211213121312
           }
           """;
        when(dispositivosService.update(anyLong(), any(DispositivoUpdateDto.class))).thenThrow(new DispositivoNotFound(id));

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                // throws DispositivoNotFoundException
                .hasFailed().failure()
                .isInstanceOf(DispositivoNotFound.class)
                .hasMessageContaining("no encontrada");

        // Verify
        verify(dispositivosService, only()).update(anyLong(), any());
    }

    @Test
    void updatePartial() {
        // Arrange
        Long id = 1L;
        String requestBody = """
           {
              "numeroSerie": 211213121312
           }
           """;

        var dispositivoSaved = DispositivoResponseDto.builder()
                .id(1L)
                .marca("Marca1")
                .modelo("Modelo1")
                .numeroSerie("211213121312")
                .fabricante("Fabricante1")
                .tipo("Tipo1")
                .build();

        when(dispositivosService.update(anyLong(), any(DispositivoUpdateDto.class))).thenReturn(dispositivoSaved);

        // Act
        var result = mockMvcTester.patch()
                .uri(ENDPOINT+ "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(DispositivoResponseDto.class)
                .isEqualTo(dispositivoSaved);

        verify(dispositivosService, only()).update(anyLong(), any(DispositivoUpdateDto.class));
    }

    @Test
    void delete() {
        // Arrange
        Long id = 1L;
        doNothing().when(dispositivosService).deleteById(anyLong());
        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT+ "/" + id)
                .exchange();
        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(dispositivosService, only()).deleteById(anyLong());

    }

    @Test
    void delete_shouldThrowDispositivoNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 3L;
        doThrow(new DispositivoNotFound(id)).when(dispositivosService).deleteById(anyLong());

        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                // throws DispositivoNotFoundException
                .hasFailed().failure()
                .isInstanceOf(DispositivoNotFound.class)
                .hasMessageContaining("no encontrada");

        // Verify
        verify(dispositivosService, only()).deleteById(anyLong());

    }
}

