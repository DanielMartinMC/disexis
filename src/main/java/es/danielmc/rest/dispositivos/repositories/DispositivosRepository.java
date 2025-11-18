package es.danielmc.rest.dispositivos.repositories;

import es.danielmc.rest.dispositivos.models.Dispositivo;
import es.danielmc.rest.titulares.models.Titular;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispositivosRepository extends JpaRepository<Dispositivo, Long>, JpaSpecificationExecutor<Dispositivo> {
    
    List<Dispositivo> findByMarca(String marca);

    @Query("SELECT d from Dispositivo d where LOWER(d.titular.nombre) like %:titular%")
    List<Dispositivo> findByTitularContainsIgnoreCase(String titular);

    @Query("SELECT d from Dispositivo d where LOWER(d.titular.nombre) like %:titular% and LOWER(d.marca) like %:marca%")
    List<Dispositivo> findByMarcaAndTitularContainsIgnoreCase(String marca, String titular);

    Optional<Dispositivo> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    List<Dispositivo> findByTitular(Titular titular);

    List<Dispositivo> findByIsDeleted(Boolean isDeleted);

    @Query("SELECT d FROM Dispositivo d WHERE d.titular.usuario.id = :usuarioId")
    Page<Dispositivo> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT d FROM Dispositivo d WHERE d.titular.usuario.id = :usuarioId")
    List<Dispositivo> findByUsuarioId(Long usuarioId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Dispositivo d WHERE d.titular.usuario.id = :id")
    Boolean existsByUsuarioId(Long id);

    @Modifying
    @Query("UPDATE Dispositivo d SET d.isDeleted = true WHERE d.id = :id")
    void updateIsDeletedToTrueById(Long id);
    

}