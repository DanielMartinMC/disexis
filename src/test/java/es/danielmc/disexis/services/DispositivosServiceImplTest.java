package es.danielmc.disexis.services;

import es.danielmc.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.exeptions.DispositivoBadUuid;
import es.danielmc.dispositivos.exeptions.DispositivoNotFound;
import es.danielmc.dispositivos.mappers.DispositivoMapper;
import es.danielmc.dispositivos.models.Dispositivo;
import es.danielmc.dispositivos.repositories.DispositivosRepository;
import es.danielmc.dispositivos.services.DispositivosServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispositivosServiceImplTest {

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
            .build();
    private DispositivoResponseDto dispositivoResponseDto1;

    @Mock
    private DispositivosRepository dispositivoRepository; // FIX: Usar la interfaz

    @Spy
    private DispositivoMapper dispositivoMapper;

    @InjectMocks
    private DispositivosServiceImpl dispositivosService;

    @Captor
    private ArgumentCaptor<Dispositivo> dispositivoCaptor;

    @BeforeEach
    void setUp() {
        dispositivoResponseDto1 = dispositivoMapper.toDispositivoResponseDto(dispositivo1);

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
        when(dispositivoRepository.findAllByMarca(marca)).thenReturn(expectedDispositivos);

        List<DispositivoResponseDto> actualDispositivoResponses = dispositivosService.findAll(marca, null);

        assertIterableEquals(expectedDispositivoResponses, actualDispositivoResponses);

        verify(dispositivoRepository, only()).findAllByMarca(marca);
    }

    @Test
    void findAll_ShouldReturnDispositivosByMarcaAndModelo_WhenBothParameterProvided(){
        String marca = "Apple";
        String modelo = "iPhone 13";
        List<Dispositivo> expectedDispositivos = List.of(dispositivo1);
        List<DispositivoResponseDto> expectedDispositivoResponses = dispositivoMapper.toDispositivoResponseDto(expectedDispositivos);
        when(dispositivoRepository.findAllByMarcaAndModelo(marca, modelo)).thenReturn(expectedDispositivos);

        List<DispositivoResponseDto> actualDispositivoResponses = dispositivosService.findAll(marca, modelo);

        assertIterableEquals(expectedDispositivoResponses, actualDispositivoResponses);

        verify(dispositivoRepository, only()).findAllByMarcaAndModelo(marca, modelo);
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
        assertEquals("Dispositivo con id " + id + " no encontrado", res.getMessage()); // FIX: Corregido el typo en el mensaje

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

        String uuid = "1234";

        var res = assertThrows(DispositivoBadUuid.class, () -> dispositivosService.findbyUuid(uuid)); // FIX: Esperar DispositivoBadUuid
        assertEquals("UUID: " + uuid + " no válido o de formato incorrecto", res.getMessage()); // FIX: Mensaje de error para UUID no válido

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
                .build();
        DispositivoResponseDto expectedDispositivoResponseDto = dispositivoMapper.toDispositivoResponseDto(expectedDispositivo);

        when(dispositivoRepository.nextId()).thenReturn(1L);
        when(dispositivoRepository.save(any(Dispositivo.class))).thenReturn(expectedDispositivo);

        DispositivoResponseDto actualDispositivoResponseDto = dispositivosService.save(dispositivoCreateDto);

        assertEquals(expectedDispositivoResponseDto, actualDispositivoResponseDto);

        verify(dispositivoRepository).nextId();
        verify(dispositivoRepository).save(dispositivoCaptor.capture());

        Dispositivo dispositivoCaptured = dispositivoCaptor.getValue();
        assertEquals(expectedDispositivo.getMarca(), dispositivoCaptured.getMarca());


    }

    @Test
    void update_ShouldReturnUpdatedDispositivo_WhenValidIdAnddispositivoUpdateProvided(){
        Long id = 1L;
        String numeroSerie = "123456789";
        when(dispositivoRepository.findById(id)).thenReturn(Optional.of(dispositivo1));

        DispositivoUpdateDto dispositivoUpdateDto = DispositivoUpdateDto.builder()
                 // Asegurar que todos los campos @NotBlank están en el DTO de actualización si se usa el mapper
                .numeroSerie(numeroSerie)
                .build();
        Dispositivo dispositivoUpdate = dispositivoMapper.toDispositivo(dispositivoUpdateDto, dispositivo1);
        when(dispositivoRepository.save(any(Dispositivo.class))).thenReturn(dispositivoUpdate);

        dispositivoResponseDto1.setNumeroSerie(numeroSerie);
        DispositivoResponseDto expectedDispositivoResponseDto = dispositivoResponseDto1;

        DispositivoResponseDto actualDispositivoResponseDto = dispositivosService.update(id, dispositivoUpdateDto);

        assertThat(actualDispositivoResponseDto)
                .usingRecursiveComparison()
                .ignoringFields("updatedAt") // FIX: Corregido typo
                .isEqualTo(expectedDispositivoResponseDto);

        verify(dispositivoRepository).findById(id);
        verify(dispositivoRepository).save(any());

    }

    @Test
    void update_ShouldThrowDispositivoNotFound_WhenInvalidIdProvided(){
        Long id = 1L;
        DispositivoUpdateDto dispositivoUpdateDto = DispositivoUpdateDto.builder()
                .numeroSerie("123456789")
                .build();
        when(dispositivoRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> dispositivosService.update(id, dispositivoUpdateDto))
                .isInstanceOf(DispositivoNotFound.class)
                .hasMessage("Dispositivo con id " + id + " no encontrado"); // FIX: Corregido el typo en el mensaje

        verify(dispositivoRepository, never()).save(any());
        verify(dispositivoRepository).findById(id);
    }

    @Test
    void deleteById_ShouldDeleteDispositivo_WhenValidIdProvided(){
        Long id = 1L;
        when(dispositivoRepository.findById(id)).thenReturn(Optional.of(dispositivo1));

        assertThatCode(() -> dispositivosService.deleteById(id)).doesNotThrowAnyException();

        verify(dispositivoRepository).findById(id);
        verify(dispositivoRepository).deleteById(id); // FIX: Añadida verificación de deleteById
    }

    @Test
    void deleteById_ShouldThrowDispositivoNotFound_WhenInvalidIdProvided(){
        Long id = 1L;
        when(dispositivoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dispositivosService.deleteById(id))
                .isInstanceOf(DispositivoNotFound.class)
                .hasMessage("Dispositivo con id " + id + " no encontrado"); // FIX: Corregido el typo en el mensaje

        verify(dispositivoRepository).findById(id); // FIX: Se verifica la llamada a findById
        verify(dispositivoRepository, never()).deleteById(anyLong()); // Se verifica que deleteById no se llamó
    }


    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }

    @Test
    void findbyUuid() {
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }
}