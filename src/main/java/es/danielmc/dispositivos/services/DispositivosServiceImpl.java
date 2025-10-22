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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = {"dispositivos"})
public class DispositivosServiceImpl implements DispositivosService {
    private final Logger logger = LoggerFactory.getLogger(DispositivosServiceImpl.class);
    private final DispositivosRepository dispositivosRepository;
    private final DispositivoMapper dispositivoMapper;

    @Autowired
    public DispositivosServiceImpl(DispositivosRepository dispositivosRepository, DispositivoMapper dispositivoMapper) {
        this.dispositivosRepository = dispositivosRepository;
        this.dispositivoMapper = dispositivoMapper;
    }

    @Override
    public List<DispositivoResponseDto> findAll(String Marca, String Modelo) {
        // Si todo está vacío o nulo, devolvemos todos los dispositivos
        if ((Marca == null || Marca.isEmpty()) && (Modelo == null || Modelo.isEmpty())) {
            logger.info("Buscando todos los dispositivos");
            return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findAll());
        }
        // Si la marca no está vacía, pero la categoría si, buscamos por marca
        if ((Marca != null && !Marca.isEmpty()) && (Modelo == null || Modelo.isEmpty())) {
            logger.info("Buscando productos por marca: " + Marca);
            return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findAllByMarca(Marca));
        }
        // Si la marca está vacía, pero la categoría no, buscamos por categoría
        if (Marca == null || Marca.isEmpty()) {
            logger.info("Buscando productos por categoría: " + Modelo);
            return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findAllByModelo(Modelo));
        }
        // Si la marca y la categoría no están vacías, buscamos por ambas
        logger.info("Buscando productos por marca: " + Marca + " y categoría: " + Modelo);
        return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findAllByMarcaAndModelo(Marca, Modelo));
    }

    @Override
    @Cacheable
    public DispositivoResponseDto findById(Long id) {
        logger.info("Buscando producto por id: " + id);
        return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findById(id)
                .orElseThrow(() -> new DispositivoNotFound(id)));
    }

    @Override
    @Cacheable
    public DispositivoResponseDto findbyUuid(String uuid) {
        logger.info("Buscando producto por uuid: " + uuid);
        var myUuid = UUID.fromString(uuid);
        return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.findByUuid(myUuid).orElseThrow(
                () -> new DispositivoNotFound(myUuid)
        ));
    }

    @CachePut
    @Override
    public DispositivoResponseDto save(DispositivoCreateDto dispositivoCreateDto) {
        logger.info("Guardando producto: " + dispositivoCreateDto);
        // obtenemos el id de producto
        Long id = dispositivosRepository.nextId();
        // Creamos el producto nuevo con los datos que nos vienen del dto, podríamos usar el mapper
        Dispositivo nuevoDispositivo = dispositivoMapper.toDispositivo(id, dispositivoCreateDto);
        // Lo guardamos en el repositorio
        return dispositivoMapper.toDispositivoResponseDto(dispositivosRepository.save(nuevoDispositivo));
    }

    @CachePut
    @Override
    public DispositivoResponseDto update(Long id, DispositivoUpdateDto dispositivoUpdateDto) {
        logger.info("Actualizando producto por id: " + id);
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
        logger.debug("Borrando producto por id: " + id);
        // Si no existe lanza excepción, por eso ya llamamos a lo que hemos implementado antes
        this.findById(id);
        // Lo borramos del repositorio
        dispositivosRepository.deleteById(id);

    }
}