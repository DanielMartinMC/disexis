package es.danielmc.disexis.dispositivos.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.danielmc.config_websockets.WebSocketConfig;
import es.danielmc.config_websockets.WebSocketHandler;
import es.danielmc.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.exceptions.DispositivoBadUuid;
import es.danielmc.dispositivos.exceptions.DispositivoNotFound;
import es.danielmc.dispositivos.mappers.DispositivoMapper;
import es.danielmc.dispositivos.models.Dispositivo;
import es.danielmc.dispositivos.repositories.DispositivosRepository;
import es.danielmc.dispositivos.services.DispositivosServiceImpl;
import es.danielmc.notification_websockets.mapppers.DispositivoNotificationMapper;
import es.danielmc.titulares.models.Titular;
import es.danielmc.titulares.services.TitularesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispositivosServiceImplTest {

    private final Titular titular = Titular.builder().id(1L).nombre("TitularTest").build();

    private final Dispositivo dispositivo1 = Dispositivo.builder()
            .id(1L)
            .marca("Samsung")
            .modelo("Galaxy S21")
            .numeroSerie("123456789")
            .fabricante("Samsung Electronics")
            .tipo("Tablet")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.randomUUID())
            .titular(titular)
            .build();

    private final Dispositivo dispositivo2 = Dispositivo.builder()
            .id(2L)
            .marca("Apple")
            .modelo("iPhone 13")
            .numeroSerie("987654321")
            .fabricante("Apple Inc.")
            .tipo("Movil")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.randomUUID())
            .titular(titular)
            .build();

    private DispositivoResponseDto dispositivoResponseDto1;

    @Mock
    private DispositivosRepository dispositivoRepository;

    @Mock
    private TitularesService titularesService; // Necesario para save()

    // Mocks para WebSockets y Notificaciones
    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private DispositivoNotificationMapper dispositivoNotificationMapper;
    @Mock
    private WebSocketHandler webSocketHandler;

    @Spy
    private DispositivoMapper dispositivoMapper;

    @InjectMocks
    private DispositivosServiceImpl dispositivosService;

    @Captor
    private ArgumentCaptor<Dispositivo> dispositivoCaptor;

    @BeforeEach
    void setUp() {
        dispositivoResponseDto1 = dispositivoMapper.toDispositivoResponseDto(dispositivo1);
        // Configuramos el mapper para que no falle al usarse en el servicio si es necesario
        // (Al ser @Spy usará la implementación real, que es correcto)
    }

    @Test
    void findAll_ShouldReturnAllDispositivos_WhenNoParametersProvided(){
        List<Dispositivo> expectedDispositivos = Arrays.asList(dispositivo1, dispositivo2);
        List<DispositivoResponseDto> expectedDispositivoResponses = dispositivoMapper.toDispositivoResponseDto(expectedDispositivos);

        when(dispositivoRepository.findAll()).thenReturn(expectedDispositivos);

        List<DispositivoResponseDto> actualDispositivoResponses = dispositivosService.findAll(null, null);

        assertIterableEquals(expectedDispositivoResponses, actualDispositivoResponses);
        verify(dispositivoRepository, only()).findAll();
    }

    @Test
    void findAll_ShouldReturnDispositivosByMarca_WhenMarcaParameterProvided(){
        String marca = "Apple";
        List<Dispositivo> expectedDispositivos = List.of(dispositivo1);
        List<DispositivoResponseDto> expectedDispositivoResponses = dispositivoMapper.toDispositivoResponseDto(expectedDispositivos);

        when(dispositivoRepository.findByMarca(marca)).thenReturn(expectedDispositivos);

        List<DispositivoResponseDto> actualDispositivoResponses = dispositivosService.findAll(marca, null);

        assertIterableEquals(expectedDispositivoResponses, actualDispositivoResponses);
        verify(dispositivoRepository, only()).findByMarca(marca);
    }

    @Test
    void findById_ShouldReturnDispositivoById_WhenValidIdProvided(){
        Long id = 1L;
        DispositivoResponseDto expectedDispositivoResponseDto = dispositivoResponseDto1;
        when(dispositivoRepository.findById(id)).thenReturn(Optional.of(dispositivo1));

        DispositivoResponseDto actualDispositivoResponseDto = dispositivosService.findById(id);

        assertEquals(expectedDispositivoResponseDto , actualDispositivoResponseDto);
        verify(dispositivoRepository, only()).findById(id);
    }

    @Test
    void findById_ShouldThrowDispositivoNotFound_WhenInvalidIdProvided(){
        Long id = 1L;
        when(dispositivoRepository.findById(id)).thenReturn(Optional.empty());

        var res = assertThrows(DispositivoNotFound.class, () -> dispositivosService.findById(id));
        assertEquals("Dispositivo con id " + id + " no encontrado", res.getMessage());

        verify(dispositivoRepository, only()).findById(id);
    }

    @Test
    void findbyUuid_ShouldReturnDispositivoByUuid_WhenValidUuidProvided(){
        UUID expectedUuid = dispositivo1.getUuid();
        DispositivoResponseDto expectedDispositivoResponseDto = dispositivoResponseDto1;
        when(dispositivoRepository.findByUuid(expectedUuid)).thenReturn(Optional.of(dispositivo1));

        DispositivoResponseDto actualDispositivoResponseDto = dispositivosService.findbyUuid(expectedUuid.toString());

        assertEquals(expectedDispositivoResponseDto , actualDispositivoResponseDto);
        verify(dispositivoRepository, only()).findByUuid(expectedUuid);
    }

    @Test
    void findbyUuid_ShouldThrowDispositivoNotFound_WhenInvalidUuidProvided(){
        String uuid = "1234"; // UUID inválido

        var res = assertThrows(DispositivoBadUuid.class, () -> dispositivosService.findbyUuid(uuid));
        assertEquals("UUID: " + uuid + " no válido o de formato incorrecto", res.getMessage());

        verify(dispositivoRepository, never()).findByUuid(any());
    }

    @Test
    void save_ShouldReturnSavedDispositivo_WhenValidDispositivoCreateDtoProvided(){
        DispositivoCreateDto dispositivoCreateDto = DispositivoCreateDto.builder()
                .marca("Samsung")
                .modelo("Galaxy S21")
                .numeroSerie("123456789")
                .fabricante("Samsung Electronics")
                .tipo("Tablet")
                .titular("TitularTest")
                .build();

        Dispositivo expectedDispositivo = Dispositivo.builder()
                .id(1L)
                .marca("Samsung")
                .modelo("Galaxy S21")
                .numeroSerie("123456789")
                .fabricante("Samsung Electronics")
                .tipo("Tablet")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .titular(titular)
                .build();

        DispositivoResponseDto expectedDispositivoResponseDto = dispositivoMapper.toDispositivoResponseDto(expectedDispositivo);

        // Mocks necesarios para el flujo de save()
        when(titularesService.findByNombre(dispositivoCreateDto.getTitular())).thenReturn(titular);
        when(dispositivoRepository.save(any(Dispositivo.class))).thenReturn(expectedDispositivo);
        // Mock del WebSocket para evitar error en onChange
        when(webSocketConfig.webSocketTarjetasHandler()).thenReturn(webSocketHandler);

        DispositivoResponseDto actualDispositivoResponseDto = dispositivosService.save(dispositivoCreateDto);

        assertEquals(expectedDispositivoResponseDto, actualDispositivoResponseDto);

        // Verificaciones
        verify(titularesService).findByNombre(dispositivoCreateDto.getTitular());
        verify(dispositivoRepository).save(dispositivoCaptor.capture());

        Dispositivo dispositivoCaptured = dispositivoCaptor.getValue();
        assertEquals(expectedDispositivo.getMarca(), dispositivoCaptured.getMarca());
    }

    @Test
    void update_ShouldReturnUpdatedDispositivo_WhenValidIdAndDispositivoUpdateProvided(){
        Long id = 1L;
        String numeroSerie = "123456789";

        DispositivoUpdateDto dispositivoUpdateDto = DispositivoUpdateDto.builder()
                .numeroSerie(numeroSerie)
                .build();

        when(dispositivoRepository.findById(id)).thenReturn(Optional.of(dispositivo1));

        // El repositorio devuelve el objeto actualizado
        Dispositivo dispositivoUpdated = dispositivoMapper.toDispositivo(dispositivoUpdateDto, dispositivo1);
        when(dispositivoRepository.save(any(Dispositivo.class))).thenReturn(dispositivoUpdated);
        // Mock del WebSocket
        when(webSocketConfig.webSocketTarjetasHandler()).thenReturn(webSocketHandler);

        dispositivoResponseDto1.setNumeroSerie(numeroSerie);
        DispositivoResponseDto expectedDispositivoResponseDto = dispositivoResponseDto1;

        DispositivoResponseDto actualDispositivoResponseDto = dispositivosService.update(id, dispositivoUpdateDto);

        assertThat(actualDispositivoResponseDto)
                .usingRecursiveComparison()
                .ignoringFields("updatedAt")
                .isEqualTo(expectedDispositivoResponseDto);

        verify(dispositivoRepository).findById(id);
        verify(dispositivoRepository).save(any());
    }

    @Test
    void deleteById_ShouldDeleteDispositivo_WhenValidIdProvided(){
        Long id = 1L;
        when(dispositivoRepository.findById(id)).thenReturn(Optional.of(dispositivo1));

        assertThatCode(() -> dispositivosService.deleteById(id)).doesNotThrowAnyException();

        verify(dispositivoRepository).findById(id);
        verify(dispositivoRepository).deleteById(id);
    }
}