package es.danielmc.disexis.dispositivos.controllers;

import es.danielmc.dispositivos.controllers.DispositivosRestController;
import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.services.DispositivosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DispositivosRestController.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "api.version=v1") // Aseguramos que la versión sea v1 para la URL
public class DispositivoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DispositivosService dispositivosService;

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

    @Test
    void getAll() throws Exception {
        var dispositivoResponses = List.of(dispositivoResponse1, dispositivoResponse2);

        // Usamos any() o isNull() para ser más flexibles con los argumentos
        when(dispositivosService.findAll(isNull(), isNull())).thenReturn(dispositivoResponses);

        mockMvc.perform(get("/api/v1/dispositivos")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) // Imprime detalles del error si falla
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].marca").value("Marca1"))
                .andExpect(jsonPath("$[1].marca").value("Marca2"));

        verify(dispositivosService, times(1)).findAll(isNull(), isNull());
    }

    @Test
    void getAllByMarca() throws Exception {
        var dispositivoResponses = List.of(dispositivoResponse2);
        String marca = "Marca2";

        // Configuramos el mock para cuando llegue la marca
        when(dispositivosService.findAll(eq(marca), isNull())).thenReturn(dispositivoResponses);

        mockMvc.perform(get("/api/v1/dispositivos")
                        .param("marca", marca) // Es mejor usar .param() que construir la URL a mano
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].marca").value("Marca2"));

        verify(dispositivosService, times(1)).findAll(eq(marca), isNull());
    }

    @Test
    void getAllByTitular() throws Exception {
        var dispositivoResponses = List.of(dispositivoResponse2);
        String titular = "Pepe";

        when(dispositivosService.findAll(isNull(), eq(titular))).thenReturn(dispositivoResponses);

        mockMvc.perform(get("/api/v1/dispositivos")
                        .param("titular", titular)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].marca").value("Marca2"));

        verify(dispositivosService, times(1)).findAll(isNull(), eq(titular));
    }
}