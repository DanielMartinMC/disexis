package es.danielmc.rest.dispositivos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.danielmc.config.websockets.WebSocketConfig;
import es.danielmc.config.websockets.WebSocketHandler;
import es.danielmc.rest.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.rest.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.rest.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.rest.dispositivos.exceptions.DispositivoBadUuid;
import es.danielmc.rest.dispositivos.exceptions.DispositivoNotFound;
import es.danielmc.rest.dispositivos.exeptions.DispositivoBadRequest;
import es.danielmc.rest.dispositivos.mappers.DispositivoMapper;
import es.danielmc.rest.dispositivos.models.Dispositivo;
import es.danielmc.rest.dispositivos.repositories.DispositivosRepository;
import es.danielmc.notification_websockets.dto.DispositivoNotificationResponse;
import es.danielmc.notification_websockets.mapppers.DispositivoNotificationMapper;
import es.danielmc.notification_websockets.models.Notificacion;
import es.danielmc.rest.titulares.models.Titular;
import es.danielmc.rest.titulares.repositories.TitularesRepository;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
@CacheConfig(cacheNames = {"dispositivos"})
@RequiredArgsConstructor
public class DispositivosServiceImpl implements DispositivosService, InitializingBean {
    private final DispositivosRepository dispositivosRepository;
    private final DispositivoMapper dispositivoMapper;
    private final TitularesRepository titularesRepository;


    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final DispositivoNotificationMapper dispositivoNotificationMapper;
    private WebSocketHandler webSocketService;

    public void afterPropertiesSet() {
        this.webSocketService = this.webSocketConfig.webSocketDispositivosHandler();
    }

    public void setWebSocketService(WebSocketHandler webSocketHandler) {
        this.webSocketService = webSocketHandler;
    }

    @Override
    public Page<DispositivoResponseDto> findAll(Optional<String> marca, Optional<String> titular, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando dispositivo por marca: {}, titular: {} , isDeleted {}", marca, titular, isDeleted);
        // Criterio de búsqueda por número
        Specification<Dispositivo> specMarcaDispositivo = (root, query, criteriaBuilder) ->
                marca.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("marca")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay numero, no filtramos

        // Criterio de búsqueda por titular
        Specification<Dispositivo> specTitularDispositivo = (root, query, criteriaBuilder) ->
                titular.map(t -> {
                    Join<Dispositivo, Titular> titularJoin = root.join("titular");
                    return criteriaBuilder.like(criteriaBuilder.lower(titularJoin.get("nombre")), "%" + t.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay titular, no filtramo

        // Criterio de búsqueda por isDeleted
        Specification<Dispositivo> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Combinamos las especificaciones
        Specification<Dispositivo> criterio = Specification.allOf(specMarcaDispositivo, specTitularDispositivo, specIsDeleted);

        return dispositivosRepository.findAll(criterio, pageable)
                .map(dispositivoMapper::toDispositivoResponseDto);
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
    public DispositivoResponseDto findByUuid(String uuid) {
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
    @Override
    public Page<DispositivoResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable) {
        log.info("Obteniendo dispositivos del usuario con id: {}", usuarioId);
        return dispositivosRepository.findByUsuarioId(usuarioId, pageable)
                .map(dispositivoMapper::toDispositivoResponseDto);
    }

    @Override
    public DispositivoResponseDto findByUsuarioId(Long usuarioId, Long idDispositivo) {
        log.info("Obteniendo dispositivos del usuario con id: {}", usuarioId);
        var dispositivos = dispositivosRepository.findByUsuarioId(usuarioId);
        var dispositivoEncontrado = dispositivos.stream().filter(t ->  t.getId().equals(idDispositivo))
                .findFirst().orElse(null);
        if (dispositivoEncontrado == null) {
            throw new DispositivoBadRequest("La dispositivo " + idDispositivo + " no corresponde a este usuario");
            // O not found también valdría
            //throw new TarjetaNotFoundException(idTarjeta);
        }
        return dispositivoMapper.toDispositivoResponseDto(dispositivoEncontrado);
    }


    /**
     * Comprueba si existe el titular
     *
     * @param nombreTitular Nombre del titular
     */
    private Titular checkTitular(String nombreTitular) {
        log.info("Buscando titular por nombre: {}", nombreTitular);
        // Buscamos el titular por su nombre, debe existir y no estar borrado
        var titular = titularesRepository.findByNombreEqualsIgnoreCase(nombreTitular);
        if (titular.isEmpty() || titular.get().getIsDeleted()) {
            throw new DispositivoBadRequest("El titular " + nombreTitular + " no existe o está borrado");
        }
        return titular.get();
    }
    @CachePut(key = "#result.id")
    @Override
    public DispositivoResponseDto save(DispositivoCreateDto dispositivoCreateDto) {
        log.info("Guardando tarjeta: {}", dispositivoCreateDto);
        Titular titular = checkTitular(dispositivoCreateDto.getTitular());
        // Creamos la tarjeta nueva con los datos que nos vienen y la guardamos en el repositorio
        Dispositivo dispositivoSaved = dispositivosRepository.save(
                dispositivoMapper.toDispositivo(dispositivoCreateDto, titular));
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.CREATE, dispositivoSaved);
        // La guardamos en el repositorio
        return dispositivoMapper.toDispositivoResponseDto(dispositivoSaved);
    }

    @Override
    public DispositivoResponseDto save(DispositivoCreateDto dispositivoCreateDto, Long usuarioId) {
        log.info("Guardando tarjeta: {} de usuarioId: {}", dispositivoCreateDto, usuarioId);
        Titular titular = checkTitular(dispositivoCreateDto.getTitular());
        var usuario = titular.getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new DispositivoBadRequest("El usuario no se corresponde con el titular");
        }
        // Creamos la tarjeta nueva con los datos que nos vienen y la guardamos en el repositorio
        Dispositivo dispositivoSaved = dispositivosRepository.save(
                dispositivoMapper.toDispositivo(dispositivoCreateDto, titular));
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.CREATE, dispositivoSaved);
        // La guardamos en el repositorio
        return dispositivoMapper.toDispositivoResponseDto(dispositivoSaved);
    }

    @CachePut(key = "#result.id")
    @Override
    public DispositivoResponseDto update(Long id, DispositivoUpdateDto dispositivoUpdateDto) {
        log.info("Actualizando tarjeta por id: {}", id);
        // Si no existe lanza excepción
        var dispositivoActual = dispositivosRepository.findById(id).orElseThrow(()-> new DispositivoNotFound(id));
        // Actualizamos la tarjeta con los datos que nos vienen y la guardamos en el repositorio
        Dispositivo dispositivoUpdated =  dispositivosRepository.save(
                dispositivoMapper.toDispositivo(dispositivoUpdateDto, dispositivoActual));
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.UPDATE, dispositivoUpdated);
        // La guardamos en el repositorio
        return dispositivoMapper.toDispositivoResponseDto(dispositivoUpdated);
    }
    @CachePut(key = "#result.id")
    @Override
    public DispositivoResponseDto update(Long id, DispositivoUpdateDto dispositivoUpdateDto, Long usuarioId) {
        log.info("Actualizando dispositivo por id: {}", id);
        // Si no existe lanza excepción
        var dispositivoActual = dispositivosRepository.findById(id).orElseThrow(()-> new DispositivoNotFound(id));
        // Como no podemos actualizar el titular, no comprobamos si existe
        // pero sí comprobamos que pertenece al usuarioId
        var usuario = dispositivoActual.getTitular().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new DispositivoBadRequest("El dispositivo con " +
                    dispositivoUpdateDto.getMarca() + " no corresponde a este usuario");
        }
        // Actualizamos la tarjeta con los datos que nos vienen y la guardamos en el repositorio
        Dispositivo dispositivoUpdated =  dispositivosRepository.save(
                dispositivoMapper.toDispositivo(dispositivoUpdateDto, dispositivoActual));
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.UPDATE, dispositivoUpdated);
        // La guardamos en el repositorio
        return dispositivoMapper.toDispositivoResponseDto(dispositivoUpdated);
    }
    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando dispositivo por id: {}", id);
        // Si no existe lanza excepción
        Dispositivo dispositivoDeleted = dispositivosRepository.findById(id).orElseThrow(()-> new DispositivoNotFound(id));
        // La borramos del repositorio si existe
        dispositivosRepository.deleteById(id);
        // O lo marcamos como borrado, para evitar problemas de cascada
        //tarjetasRepository.updateIsDeletedToTrueById(id);
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.DELETE, dispositivoDeleted);

    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id, Long usuarioId) {
        log.debug("Borrando dispositivo por id: {}", id);
        // Si no existe lanza excepción
        Dispositivo dispositivoDeleted = dispositivosRepository.findById(id).orElseThrow(()-> new DispositivoNotFound(id));
        // La borramos del repositorio si existe y si pertenece al usuario
        var usuario = dispositivoDeleted.getTitular().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new DispositivoBadRequest("La dispositivo " + id + " no corresponde a este usuario");
        }
        dispositivosRepository.deleteById(id);
        // O lo marcamos como borrado, para evitar problemas de cascada
        //tarjetasRepository.updateIsDeletedToTrueById(id);
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.DELETE, dispositivoDeleted);

    }

    void onChange(Notificacion.Tipo tipo, Dispositivo data) {
        log.debug("Servicio de productos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketDispositivosHandler();
        }

        try {
            Notificacion<DispositivoNotificationResponse> notificacion = new Notificacion<>(
                    "DISPOSITIVO",
                    tipo,
                    dispositivoNotificationMapper.toDispositivoNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");
            // Enviamos el mensaje a los clientes ws con un hilo, si hay muchos clientes, puede tardar.
            // No bloqueamos el hilo principal que atiende las peticiones http
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.setName("WebSocketDispositivo-" + data.getId());
            senderThread.setDaemon(true); // Para que no impida que la aplicación se cierre
            senderThread.start();
            log.info("Hilo de websocket iniciado: {}", data.getId());
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }
}