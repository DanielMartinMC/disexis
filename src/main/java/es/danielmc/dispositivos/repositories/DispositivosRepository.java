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
    
    List<Dispositivo> findAllByMarcaAndTitular(String marca, String titular);

    List<Dispositivo> findByTitularContainsIgnoreCase(String titular);

    List<Dispositivo> findByMarcaContainsIgnoreCaseAndIsDeletedFalse(String marca);
    List<Dispositivo> findByMarcaAndTitularContainsIgnoreCase(String marca, String titular);
    
    List<Dispositivo> findByMarcaAndTitularContainsIgnoreCaseAndIsDeletedFalse(String marca, String titular);
    
    Optional<Dispositivo> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);
    
    @Modifying
    @Query("UPDATE Dispositivo d SET d.isDeleted = true WHERE d.id = :id")
    
    void updateIsDeletedToTrueById(Long id);
    

}