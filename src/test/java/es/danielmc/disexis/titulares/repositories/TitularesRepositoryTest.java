package es.danielmc.disexis.titulares.repositories;

import es.danielmc.rest.titulares.models.Titular;
import es.danielmc.rest.titulares.repositories.TitularesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Ejecuta reset.sql antes de cada método para limpiar la BD
@Sql(value = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class TitularesRepositoryTest {

    private final Titular titular = Titular.builder().nombre("Jose").build();

    @Autowired
    private TitularesRepository repositorio;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Guardamos el titular "Jose".
        // IMPORTANTE: Al hacer persist, JPA actualiza el objeto 'titular' con el ID real que la BD le ha asignado.
        entityManager.persist(titular);
        entityManager.flush();
    }

    @Test
    void findAll() {
        List<Titular> titulares = repositorio.findAll();

        assertAll("findAll",
                () -> assertNotNull(titulares),
                () -> assertFalse(titulares.isEmpty()),
                // Como reset.sql borró todo y setUp metió 1, debería haber 1
                () -> assertEquals(1, titulares.size())
        );
    }

    @Test
    void findByNombre() {
        List<Titular> titulares = repositorio.findByNombreContainingIgnoreCase("Jose");

        assertAll("findByNombre",
                () -> assertNotNull(titulares),
                () -> assertFalse(titulares.isEmpty()),
                () -> assertEquals("Jose", titulares.get(0).getNombre())
        );
    }

    @Test
    void findById() {
        // ERROR CORREGIDO: No buscamos el 1L, buscamos titular.getId()
        // Así el test funciona tenga el ID que tenga (1, 3, 50...)
        Titular titularEncontrado = repositorio.findById(titular.getId()).orElse(null);

        assertAll("findById",
                () -> assertNotNull(titularEncontrado),
                () -> assertEquals("Jose", titularEncontrado.getNombre())
        );
    }

    @Test
    void findByIdNotFound() {
        // Buscamos un ID que seguro no existe (el actual + 999)
        Titular titularNoExiste = repositorio.findById(titular.getId() + 999L).orElse(null);

        assertNull(titularNoExiste);
    }

    @Test
    void save() {
        Titular nuevo = repositorio.save(Titular.builder().nombre("Pepe").build());

        assertAll("save",
                () -> assertNotNull(nuevo),
                () -> assertNotNull(nuevo.getId()),
                () -> assertEquals("Pepe", nuevo.getNombre())
        );
    }

    @Test
    void update() {
        // 1. Recuperamos el titular existente usando su ID real
        var titularExistente = repositorio.findById(titular.getId()).orElseThrow();

        // 2. Modificamos manteniendo el MISMO ID
        Titular titularActualizar = Titular.builder()
                .id(titularExistente.getId())
                .nombre("Jose_Updated")
                .build();

        Titular titularActualizado = repositorio.save(titularActualizar);

        assertAll("update",
                () -> assertNotNull(titularActualizado),
                () -> assertEquals("Jose_Updated", titularActualizado.getNombre()),
                () -> assertEquals(titularExistente.getId(), titularActualizado.getId())
        );
    }

    @Test
    void delete() {
        // Recuperamos el titular usando su ID real
        var titularBorrar = repositorio.findById(titular.getId()).orElseThrow();

        repositorio.delete(titularBorrar);

        // Verificamos que ya no existe
        Titular titularBorrado = repositorio.findById(titular.getId()).orElse(null);

        assertNull(titularBorrado);
    }

    @Test
    void test_FetchType_EAGER_vs_LAZY() {
        entityManager.clear();
        Titular titularEncontrado = repositorio.findById(titular.getId()).orElse(null);
        assertNotNull(titularEncontrado);
    }
}