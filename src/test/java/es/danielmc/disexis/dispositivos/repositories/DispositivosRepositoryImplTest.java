package es.danielmc.disexis.dispositivos.repositories;

import es.danielmc.dispositivos.models.Dispositivo;
import es.danielmc.dispositivos.repositories.DispositivosRepository;
import es.danielmc.titulares.models.Titular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// Reseteamos la base de datos para partir de una situación conocida
@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
// Vamos a probar el repositorio, pero moqueamos la base de datos JPA
@DataJpaTest
class DispositivosRepositoryImplTest {

    private final Titular titular1 = Titular.builder().nombre("Jose").build();
    private final Titular titular2 = Titular.builder().nombre("Juan").build();

    private final Dispositivo dispositivo1 = Dispositivo.builder()
            .marca("Samsung")
            .modelo("Galaxy S21")
            .numeroSerie("1234-5678-1234-5678")
            .fabricante("Samsung Electronics")
            .tipo("Tablet")
            .titular(titular1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .isDeleted(false)
            .build();

    private final Dispositivo dispositivo2 = Dispositivo.builder()
            .marca("Apple")
            .modelo("iPhone 13")
            .numeroSerie("4321-5678-1234-5678")
            .fabricante("Apple Inc.")
            .tipo("Movil")
            .titular(titular2)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .isDeleted(false)
            .build();

    @Autowired
    private DispositivosRepository repositorio;
    @Autowired
    private TestEntityManager entityManager; // EntityManager para hacer las pruebas

    @BeforeEach
    void setUp() {
        // Vamos a salvar un titular
        entityManager.persist(titular1);
        entityManager.persist(titular2);
        // Vamos a salvar dos dispositivos
        entityManager.persist(dispositivo1);
        entityManager.persist(dispositivo2);
        entityManager.flush();
    }

    @Test
    void findAll() {
        // Act
        List<Dispositivo> dispositivos = repositorio.findAll();

        // Assert
        assertAll("findAll",
                () -> assertNotNull(dispositivos),
                () -> assertEquals(2, dispositivos.size())
        );
    }

    @Test
    void findAllByNumero() { // Equivalente a tu findByMarca en Dispositivos
        // Act
        String marca = "Apple";
        List<Dispositivo> dispositivos = repositorio.findByMarca(marca);

        // Assert
        assertAll("findAllByMarca",
                () -> assertNotNull(dispositivos),
                () -> assertEquals(1, dispositivos.size()),
                () -> assertEquals(marca, dispositivos.getFirst().getMarca())
        );
    }

    @Test
    void findAllByTitular() {
        // Act
        String titular = "Jose";
        List<Dispositivo> dispositivos = repositorio.findByTitularContainsIgnoreCase(titular.toLowerCase());

        // Assert
        assertAll("findAllByTitular",
                () -> assertNotNull(dispositivos),
                () -> assertEquals(1, dispositivos.size()),
                () -> assertEquals(titular, dispositivos.getFirst().getTitular().getNombre())
        );
    }

    @Test
    void findAllByMarcaAndTitular() { // Equivalente a findByMarcaAndTitular...
        // Act
        String marca = "Apple";
        String titular = "Juan";
        List<Dispositivo> dispositivos = repositorio.findByMarcaAndTitularContainsIgnoreCase(marca, titular.toLowerCase());

        // Assert
        assertAll("findAllByMarcaAndTitular",
                () -> assertNotNull(dispositivos),
                () -> assertEquals(1, dispositivos.size()),
                () -> assertEquals(marca, dispositivos.getFirst().getMarca()),
                () -> assertEquals(titular, dispositivos.getFirst().getTitular().getNombre())
        );
    }

    @Test
    void findById_existingId_returnsOptionalWithDispositivo() {
        // Act
        // USAMOS EL ID REAL QUE LA BD HA ASIGNADO PARA QUE NO FALLE NUNCA
        Long id = dispositivo1.getId();
        Optional<Dispositivo> optionalDispositivo = repositorio.findById(id);

        // Assert
        assertAll("findById_existingId_returnsOptionalWithDispositivo",
                () -> assertNotNull(optionalDispositivo),
                () -> assertTrue(optionalDispositivo.isPresent()),
                () -> assertEquals(id, optionalDispositivo.get().getId())
        );
    }

    @Test
    void findById_nonExistingId_returnsEmptyOptional() {
        // Act
        Long id = 999L; // ID inventado
        Optional<Dispositivo> optionalDispositivo = repositorio.findById(id);

        // Assert
        assertAll("findById_nonExistingId_returnsEmptyOptional",
                () -> assertNotNull(optionalDispositivo),
                () -> assertTrue(optionalDispositivo.isEmpty())
        );
    }

    @Test
    void findByUuid_existingUuid_returnsOptionalWithDispositivo() {
        // Act
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        Optional<Dispositivo> optionalDispositivo = repositorio.findByUuid(uuid);

        // Assert
        assertAll("findByUuid_existingUuid_returnsOptionalWithDispositivo",
                () -> assertNotNull(optionalDispositivo),
                () -> assertTrue(optionalDispositivo.isPresent()),
                () -> assertEquals(uuid, optionalDispositivo.get().getUuid())
        );
    }

    @Test
    void findByUuid_nonExistingUuid_returnsEmptyOptional() {
        // Act
        UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
        Optional<Dispositivo> optionalDispositivo = repositorio.findByUuid(uuid);

        // Assert
        assertAll("findByUuid_nonExistingUuid_returnsEmptyOptional",
                () -> assertNotNull(optionalDispositivo),
                () -> assertTrue(optionalDispositivo.isEmpty())
        );
    }

    @Test
    void existsById_existingId_returnsTrue() {
        // Act
        Long id = dispositivo1.getId();
        boolean exists = repositorio.existsById(id);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        // Act
        Long id = 999L;
        boolean exists = repositorio.existsById(id);

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByUuid_existingUuid_returnsTrue() {
        // Act
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        boolean exists = repositorio.existsByUuid(uuid);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUuid_nonExistingUuid_returnsFalse() {
        // Act
        UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
        boolean exists = repositorio.existsByUuid(uuid);

        // Assert
        assertFalse(exists);
    }

    @Test
    void save_notExists() {
        // Arrange
        Dispositivo dispositivo = Dispositivo.builder()
                .marca("Xiaomi")
                .modelo("Redmi Note 10")
                .numeroSerie("SN-NEW-112233")
                .fabricante("Xiaomi Corp")
                .tipo("Movil")
                .titular(titular1)
                .uuid(UUID.randomUUID())
                .isDeleted(false)
                .build();

        // Act
        Dispositivo savedDispositivo = repositorio.save(dispositivo);
        var all = repositorio.findAll();

        // Assert
        // Comprueba que ha creado una nueva tarjeta
        assertAll("save",
                () -> assertNotNull(savedDispositivo),
                () -> assertEquals(dispositivo, savedDispositivo),
                () -> assertEquals(3, all.size()) // 2 iniciales + 1 nueva
        );
    }

    @Test
    void save_butExists() {
        // Arrange
        // Recuperamos el ID real para asegurar que actualizamos
        Long id = dispositivo1.getId();

        Dispositivo dispositivoExistente = Dispositivo.builder()
                .id(id)
                .marca("Samsung")
                .modelo("Galaxy S21")
                .numeroSerie("1234-5678-1234-5678")
                .fabricante("Samsung Electronics")
                .tipo("Tablet")
                .titular(titular1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
                .isDeleted(false)
                .build();

        // Act
        Dispositivo savedDispositivo = repositorio.save(dispositivoExistente);
        var all = repositorio.findAll();

        // Assert
        // Comprueba que actualiza una tarjeta existente
        assertAll("save",
                () -> assertNotNull(savedDispositivo),
                () -> assertTrue(repositorio.existsById(id)),
                () -> assertTrue(all.size() >= 2)
        );
    }

    @Test
    void deleteById_existingId() {
        // Act
        Long id = dispositivo1.getId();
        repositorio.deleteById(id);
        var all = repositorio.findAll();

        // Assert
        assertAll("deleteById_existingId",
                () -> assertEquals(1, all.size()), // Queda 1
                () -> assertFalse(repositorio.existsById(id))
        );
    }

    @Test
    void deleteByUuid_existingUuid() {
        // Act
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        repositorio.deleteByUuid(uuid);
        var all = repositorio.findAll();

        // Assert
        assertAll("deleteByUuid_existingUuid",
                () -> assertEquals(1, all.size()), // Queda 1
                () -> assertFalse(repositorio.existsByUuid(uuid))
        );
    }
}