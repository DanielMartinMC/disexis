package es.danielmc.dispositivos.repositories;

import es.danielmc.dispositivos.models.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispositivosRepository extends JpaRepository <Dispositivo, Long> {
    List<Dispositivo> findAll();

    List<Dispositivo> findAllById(Long id);

    List<Dispositivo> findAllById(String id);

    List<Dispositivo> findAllByMarca(String Marca);

    List<Dispositivo> findAllByModelo(String modelo);

    List<Dispositivo> findAllByNumeroSerie(String numeroSerie);

    List<Dispositivo> findAllByFabricante(String fabricante);

    List<Dispositivo> findAllByTipo(String tipo);

    List<Dispositivo> findAllByMarcaAndModelo(String Marca, String modelo);

    Optional<Dispositivo> findById(Long id);

    Optional<Dispositivo> findByUuid(UUID uuid);

    boolean existsById(Long id);

    boolean existsByUuid(UUID uuid);

    Dispositivo save(Dispositivo Dispositivo);

    void deleteById(Long id);

    void deleteByUuid(UUID uuid);

    Long nextId();

}