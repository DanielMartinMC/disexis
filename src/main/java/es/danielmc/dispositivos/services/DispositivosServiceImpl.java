package es.danielmc.dispositivos.services;

import es.danielmc.dispositivos.dto.DispositivoCreateDto;
/*import es.danielmc.dispositivos.exceptions.ProductoNotFound; */
import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.exeptions.DispositivoBadUuid;
import es.danielmc.dispositivos.exeptions.DispositivoNotFound;
import es.danielmc.dispositivos.mappers.DispositivoMapper;
import es.danielmc.dispositivos.models.Dispositivo;
import es.danielmc.dispositivos.repositories.DispositivosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@CacheConfig(cacheNames = {"dispositivos"})
@RequiredArgsConstructor
public class DispositivosServiceImpl implements DispositivosService {
    private final DispositivosRepository dispositivosRepository;
    private final DispositivoMapper dispositivoMapper;

   

    @Override
    public List<DispositivoResponseDto> findAll(String marca, String titular) {
        // Si todo está vacío o nulo, devolvemos todos los dispositivos
        if ((marca == null || marca.isEmpty()) && (titular == null || titular.isEmpty())) {
            log.info("Buscando todos los dispositivos");
            return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findAll());
        }
        // Si la marca no está vacía, pero la categoría si, buscamos por marca
        if ((marca != null && !marca.isEmpty()) && (titular == null || titular.isEmpty())) {
            log.info("Buscando productos por marca: " + marca);
            return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findByMarca(marca));
        }
        // Si la marca está vacía, pero la categoría no, buscamos por categoría
        if (marca == null || marca.isEmpty()) {
            log.info("Buscando productos por categoría: " + titular);
            return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findByTitularContainsIgnoreCase( titular));
        }
        // Si la marca y la categoría no están vacías, buscamos por ambas
        log.info("Buscando productos por marca: " + marca + " y categoría: " + titular);
        return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findByMarcaAndTitularContainsIgnoreCase(marca, titular));
    }

    @Override
    @Cacheable(key = "#id")
    public DispositivoResponseDto findById(Long id) {
        log.info("Buscando producto por id: " + id);
        return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findById(id)
                .orElseThrow(() -> new DispositivoNotFound(id)));
    }

    @Override
    @Cacheable
    public DispositivoResponseDto findbyUuid(String uuid) {
        log.info("Buscando producto por uuid: " + uuid);
        UUID myUuid;
        // FIX: Se añade try-catch para lanzar la excepción correcta en caso de UUID no válido
        try {
            myUuid = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new DispositivoBadUuid(uuid);
        }

        return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findByUuid(myUuid).orElseThrow(
                () -> new DispositivoNotFound(myUuid)
        ));
    }

    @CachePut
    @Override
    public DispositivoResponseDto save(DispositivoCreateDto dispositivoCreateDto) {
        log.info("Guardando producto: " + dispositivoCreateDto);
        // obtenemos el id de producto
        // Creamos el producto nuevo con los datos que nos vienen del dto, podríamos usar el mapper
        Dispositivo nuevoDispositivo = dispositivoMapper.toDispositivo(dispositivoCreateDto);
        // Lo guardamos en el repositorio
        return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.save(nuevoDispositivo));
    }

    @CachePut
    @Override
    public DispositivoResponseDto update(Long id, DispositivoUpdateDto dispositivoUpdateDto) {
        log.info("Actualizando producto por id: " + id);
        // Si no existe lanza excepción, por eso ya llamamos a lo que hemos implementado antes
        var dispositivoActual = dispositivosRepository.findById(id).orElseThrow(() -> new DispositivoNotFound(id));
        // Actualizamos el producto con los datos que nos vienen del dto, podríamos usar el mapper
        Dispositivo productoActualizado = dispositivoMapper.toDispositivo(dispositivoUpdateDto, dispositivoActual);
        // Lo guardamos en el repositorio
        return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.save(productoActualizado));
    }

    @Override
    @CacheEvict
    public void deleteById(Long id) {
        log.debug("Borrando producto por id: " + id);
        // Si no existe lanza excepción, por eso ya llamamos a lo que hemos implementado antes
        this.findById(id);
        // Lo borramos del repositorio
        dispositivosRepository.deleteById(id);

    }
}