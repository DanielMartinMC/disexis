package es.danielmc.disexis.controllers;

import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.services.DispositivosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
            .tipo("Movil")
            .build();
    private final DispositivoResponseDto dispositivoResponse2 = DispositivoResponseDto.builder()
            .id(2L)
            .marca("Marca2")
            .modelo("Modelo2")
            .numeroSerie("0987654321")
            .fabricante("Fabricante2")
            .tipo("Tablet")
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

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
        String queryString = "?marca=" + dispositivoResponse2.getMarca();

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse2);
                });
        verify(dispositivosService, times(1)).findAll(anyString(), isNull());
    }
    @Test
    void getAllByModelo() {
        String queryString = "?modelo=" + dispositivoResponse2.getModelo();

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(DispositivoResponseDto.class).isEqualTo(dispositivoResponse2);
                });

        verify(dispositivosService, times(1)).findAll(isNull(), anyString());
    }

    @Test
    void getAllByMarcaAndModelo() {
        String queryString = "?marca=" + dispositivoResponse2.getMarca() + "&modelo=" + dispositivoResponse2.getModelo();

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();
    }
}

