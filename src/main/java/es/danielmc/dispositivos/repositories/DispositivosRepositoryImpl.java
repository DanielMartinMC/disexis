package es.danielmc.dispositivos.repositories;

import es.danielmc.dispositivos.models.Dispositivo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class DispositivosRepositoryImpl implements DispositivosRepository {

    private final Map<Long, Dispositivo> Dispositivos = new LinkedHashMap<>(
            Map.of(
                    1L, new Dispositivo(1L,
                            "Samsung",
                            "Galaxy S21",
                            "123456789",
                            "Samsung Electronics",
                            "movil",
                            null,
                            LocalDateTime.of(2025,12,31,0,0), LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID()),
                    2L, new Dispositivo(2L, "Apple", "iPhone 13", "987654321", "Apple Inc.", "movil",null,
                            LocalDateTime.of(2025,12,31,0,0), LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID())
            ));


    @Override
    public List<Dispositivo> findAll() {
        log.info("Buscando Dispositivos");
        return Dispositivos.values().stream()
                .toList();
    }

    @Override
    public List<Dispositivo> findAllById(Long id) {
        return List.of();
    }

    @Override
    public List<Dispositivo> findAllById(String id) {
        log.info("Buscando Dispositivos por id: " + id);
        return Dispositivos.values().stream()
                .filter(dispositivo -> dispositivo.getMarca().toLowerCase().contains(id.toLowerCase()))
                .toList();
    }
    @Override
    public List<Dispositivo> findAllByMarca(String Marca) {
        log.info("Buscando Dispositivos por Marca: " + Marca);
        String lower = Marca == null ? "" : Marca.toLowerCase();
        return Dispositivos.values().stream()
                .filter(dispositivo -> dispositivo.getMarca() != null && dispositivo.getMarca().toLowerCase().contains(lower))
                .toList();
    }
    @Override
    public List<Dispositivo> findAllByModelo(String modelo) {
        log.info("Buscando Dispositivos por Modelo: " + modelo);
        String lower = modelo == null ? "" : modelo.toLowerCase();
        return Dispositivos.values().stream()
                .filter(dispositivo -> dispositivo.getModelo() != null && dispositivo.getModelo().toLowerCase().contains(lower))
                .toList();
    }
    @Override
    public List<Dispositivo> findAllByNumeroSerie(String numeroSerie) {
        log.info("Buscando Dispositivos por Numero de Serie: " + numeroSerie);
        String lower = numeroSerie == null ? "" : numeroSerie.toLowerCase();
        return Dispositivos.values().stream()
                .filter(dispositivo -> dispositivo.getNumeroSerie() != null && dispositivo.getNumeroSerie().toLowerCase().contains(lower))
                .toList();
    }
    @Override
    public List<Dispositivo> findAllByFabricante(String fabricante) {
        log.info("Buscando Dispositivos por Fabricante: " + fabricante);
        String lower = fabricante == null ? "" : fabricante.toLowerCase();
        return Dispositivos.values().stream()
                .filter(dispositivo -> dispositivo.getFabricante() != null && dispositivo.getFabricante().toLowerCase().contains(lower))
                .toList();
    }
    @Override
    public List<Dispositivo> findAllByTipo(String tipo) {
        log.info("Buscando Dispositivos por Tipo: " + tipo);
        String lower = tipo == null ? "" : tipo.toLowerCase();
        return Dispositivos.values().stream()
                .filter(dispositivo -> dispositivo.getTipo() != null && dispositivo.getTipo().toLowerCase().contains(lower))
                .toList();
    }

    @Override public List<Dispositivo> findAllByMarcaAndModelo(String Marca, String Modelo) {
        log.info("Buscando Dispositivos por id: {} y Modelo: {} ", Marca, Modelo);
            return Dispositivos.values().stream()
                    .filter(dispositivo -> dispositivo.getMarca().toLowerCase().contains(Marca.toLowerCase()))
                    .filter(dispositivo -> dispositivo.getModelo().toLowerCase().contains(Modelo.toLowerCase()))
                    .toList();
    }

    @Override
    public Optional<Dispositivo> findById(Long id) {
        log.info("Buscando Dispositivos por id: " + id);
        return Dispositivos.get(id) != null ? Optional.of(Dispositivos.get(id)) : Optional.empty();
    }

    @Override
    public Optional<Dispositivo> findByUuid(UUID uuid) {
        log.info("Buscando Dispositivos por uuid: " + uuid);
        return Dispositivos.values().stream()
                .filter(dispositivo -> dispositivo.getUuid().equals(uuid))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        log.info("Comprobando si existe Dispositivos por id: " + id);
        return Dispositivos.get(id) != null;
    }

    @Override
    public boolean existsByUuid(UUID uuid) {
        log.info("Comprobando si existe Dispositivos por uuid: " + uuid);
        return Dispositivos.values().stream()
                .anyMatch(dispositivo -> dispositivo.getUuid().equals(uuid));
    }

    @Override
    public Dispositivo save(Dispositivo dispositivo) {
        log.info("Guardando Dispositivos: " + dispositivo);
        Dispositivos.put(dispositivo.getId(), dispositivo);
        return dispositivo;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Borrando Dispositivos por id: " + id);
        Dispositivos.remove(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        log.info("Borrando Dispositivos por uuid: " + uuid);
        Dispositivos.values().removeIf(dispositivo -> dispositivo.getUuid().equals(uuid));
    }

    @Override
    public Long nextId() {
        log.debug("Obteniendo siguiente id de Dispositivos");
        return Dispositivos.keySet().stream()
                .mapToLong(value -> value)
                .max()
                .orElse(0) + 1;
    }
}