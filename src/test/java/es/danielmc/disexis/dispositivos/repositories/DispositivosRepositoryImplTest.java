package es.danielmc.disexis.dispositivos.repositories;

import es.danielmc.dispositivos.models.Dispositivo;
import es.danielmc.dispositivos.repositories.DispositivosRepository;
import es.danielmc.titulares.models.Titular;
import es.danielmc.titulares.repositories.TitularesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DispositivosRepositoryImplTest {

    @Autowired
    private DispositivosRepository repository;

    @Autowired
    private TitularesRepository titularesRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Dispositivo dispositivo1;
    private Dispositivo dispositivo2;
    private Titular titular;

    @BeforeEach
    void setUp() {
        // Necesitamos un titular para los dispositivos
        titular = Titular.builder().nombre("TitularRepo").build();
        titularesRepository.save(titular);

        dispositivo1 = Dispositivo.builder()
                .marca("Samsung")
                .modelo("Galaxy S21")
                .numeroSerie("123456789")
                .fabricante("Samsung Electronics")
                .tipo("Tablet")
                .uuid(UUID.randomUUID())
                .titular(titular)
                .build();

        dispositivo2 = Dispositivo.builder()
                .marca("Apple")
                .modelo("iPhone 13")
                .numeroSerie("987654321")
                .fabricante("Apple Inc.")
                .tipo("Movil")
                .uuid(UUID.randomUUID())
                .titular(titular)
                .build();

        repository.save(dispositivo1);
        repository.save(dispositivo2);
    }

    @Test
    void findAll_ShouldReturnAllDispositivos() {
        List<Dispositivo> dispositivos = repository.findAll();
        assertFalse(dispositivos.isEmpty());
        assertTrue(dispositivos.size() >= 2);
    }

    @Test
    void findByMarca_ShouldReturnDispositivos() {
        List<Dispositivo> result = repository.findByMarca("Apple");
        assertFalse(result.isEmpty());
        assertEquals("Apple", result.get(0).getMarca());
    }

    @Test
    void findByUuid_ShouldReturnDispositivo() {
        Optional<Dispositivo> result = repository.findByUuid(dispositivo1.getUuid());
        assertTrue(result.isPresent());
        assertEquals(dispositivo1.getNumeroSerie(), result.get().getNumeroSerie());
    }

    @Test
    void deleteByUuid_ShouldDelete() {
        // En JPA deleteBy suele requerir transacción, @DataJpaTest la maneja
        repository.deleteByUuid(dispositivo1.getUuid());
        Optional<Dispositivo> result = repository.findByUuid(dispositivo1.getUuid());
        assertTrue(result.isEmpty());
    }
}