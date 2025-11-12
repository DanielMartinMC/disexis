package es.danielmc.disexis.repositories;

import es.danielmc.dispositivos.models.Dispositivo;
import es.danielmc.dispositivos.repositories.DispositivosRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class DispositivosRepositoryImplTest {
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

    private DispositivosRepositoryImpl repository;

    @BeforeEach
     void setUp() {
        repository = new DispositivosRepositoryImpl();
        repository.save(dispositivo1);
        repository.save(dispositivo2);
    }

    @Test
     void findAll() {
        List<Dispositivo> dispositivos = repository.findAll();

        assertAll("findAll",
                () -> assertNotNull(dispositivos),
                () -> assertEquals(2, dispositivos.size())
        );
    }

    @Test
     void findAllByMarca() {
        String marca = "Apple";
        List<Dispositivo> dispositivos = repository.findAllByMarca(marca);

        assertAll("findAllByMarca",
                () -> assertNotNull(dispositivos),
                () -> assertEquals(1, dispositivos.size()),
                () -> assertEquals(marca, dispositivos.get(0).getMarca())
        );
    }

    @Test
    void findAllByModelo() {
        String modelo = "iPhone 13";
        List<Dispositivo> dispositivos = repository.findAllByModelo(modelo);

        assertAll("findAllByModelo",
                () -> assertNotNull(dispositivos),
                () -> assertEquals(1, dispositivos.size()),
                () -> assertEquals(modelo, dispositivos.get(0).getModelo())
        );
    }

    @Test

    void findAllByMarcaAndModelo(){
        String marca = "Apple";
        String modelo = "iPhone 13";
        List<Dispositivo> dispositivos = repository.findAllByMarcaAndModelo(marca, modelo);

        assertAll("findAllByMarcaAndModelo",
                () -> assertNotNull(dispositivos),
                () -> assertEquals(1, dispositivos.size()),
                () -> assertEquals(marca, dispositivos.get(0).getMarca()),
                () -> assertEquals(modelo, dispositivos.get(0).getModelo())
        );
    }

    @Test
    void findById_exitingId_returnOptionalWithDispositivo() {
        Long id = 1L;
        Optional<Dispositivo> dispositivo = repository.findById(id);

        assertAll("findById",
                () -> assertNotNull(dispositivo),
                () -> assertTrue(dispositivo.isPresent()),
                () -> assertEquals(dispositivo1, dispositivo.get())

        );
    }

    @Test
    void findById_nonExitingId_returnEmptyOptional() {
        Long id = 999L;
        Optional<Dispositivo> dispositivo = repository.findById(id);

        assertAll("findById",
                () -> assertNotNull(dispositivo),
                () -> assertTrue(dispositivo.isEmpty())
        );
    }

    @Test
    void findByUuid() {
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478")     ;
        Optional<Dispositivo> dispositivo = repository.findByUuid(uuid);

        assertAll("findByUuid",
                () -> assertNotNull(dispositivo),
                () -> assertTrue(dispositivo.isPresent()),
                () -> assertEquals(dispositivo1, dispositivo.get())

        );
    }

    @Test

    void findByUuid_nonExistingUuid_returnEmptyOptional() {
        UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
        Optional<Dispositivo> dispositivo = repository.findByUuid(uuid);

        assertAll("findByUuid",
                () -> assertNotNull(dispositivo),
                () -> assertTrue(dispositivo.isEmpty())
        );
    }

    @Test
    void existsById_existingId_returnTrue() {
        Long id = 1L;
        boolean exists = repository.existsById(id);

        assertTrue(exists);
    }

    @Test
    void existsById_nonExistingId_returnFalse() {
        Long id = 999L;
        boolean exists = repository.existsById(id);

        assertFalse(exists);
    }

    @Test
    void existsByUuid_existingUuid_returnTrue() {
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        boolean exists = repository.existsByUuid(uuid);

        assertTrue(exists);
    }

    @Test
    void existsByUuid_nonExistingUuid_returnFalse() {
        UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
        boolean exists = repository.existsByUuid(uuid);

        assertFalse(exists);
    }

    @Test
    void save_notExists(){
        Dispositivo dispositivo = Dispositivo.builder()
                .id(3L)
                .marca("Xiami")
                .modelo("Redmi Note 10")
                .numeroSerie("1122334455")
                .fabricante("Xiami Corporation")
                .tipo("Movil")
                .build();

        Dispositivo saved = repository.save(dispositivo);
        var all = repository.findAll();

        assertAll("save",
                () -> assertNotNull(saved),
                () -> assertEquals(3L, saved.getId()),
                () -> assertEquals(all.size(), 3)
        );

    }

    @Test
    void deleteById_existingId(){
        Long id = 1L;
        repository.deleteById(id);
        var all = repository.findAll();

        assertAll("deleteById_existingId",
                () -> assertEquals(all.size(), 1),
                () -> assertFalse(repository.existsById(id))
        );
    }

    @Test
    void deleteByUuid_existingUuid(){
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        repository.deleteByUuid(uuid);
        var all = repository.findAll();

        assertAll("deleteByUuid_existingUuid",
                () -> assertEquals(all.size(), 1),
                () -> assertFalse(repository.existsByUuid(uuid))
        );
    }

    @Test
    void nextId(){
        Long id = repository.nextId();
        assertAll("nextId",
                () -> assertNotNull(id),
                () -> assertTrue(id > 0)
        );
    }





}
