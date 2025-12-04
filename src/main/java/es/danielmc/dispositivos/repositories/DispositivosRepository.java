package es.danielmc.dispositivos.repositories;

import es.danielmc.dispositivos.models.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispositivosRepository extends JpaRepository<Dispositivo, Long> {
    
    List<Dispositivo> findByMarca(String marca);

    @Query("SELECT d from Dispositivo d where LOWER(d.titular.nombre) like %:titular%")
    List<Dispositivo> findByTitularContainsIgnoreCase(String titular);

    @Query("SELECT d from Dispositivo d where LOWER(d.titular.nombre) like %:titular% and LOWER(d.marca) like %:marca%")
    List<Dispositivo> findByMarcaAndTitularContainsIgnoreCase(String marca, String titular);

    Optional<Dispositivo> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    List<Dispositivo> findByIsDeleted(Boolean isDeleted);

    @Modifying
    @Query("UPDATE Dispositivo d SET d.isDeleted = true WHERE d.id = :id")
    void updateIsDeletedToTrueById(Long id);
    

}