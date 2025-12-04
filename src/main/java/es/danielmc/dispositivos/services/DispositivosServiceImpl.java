package es.danielmc.dispositivos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.danielmc.config_websockets.WebSocketConfig;
import es.danielmc.config_websockets.WebSocketHandler;
import es.danielmc.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.exceptions.DispositivoBadUuid;
import es.danielmc.dispositivos.exceptions.DispositivoNotFound;
import es.danielmc.dispositivos.mappers.DispositivoMapper;
import es.danielmc.dispositivos.models.Dispositivo;
import es.danielmc.dispositivos.repositories.DispositivosRepository;
import es.danielmc.notification_websockets.dto.DispositivoNotificationResponse;
import es.danielmc.notification_websockets.mapppers.DispositivoNotificationMapper;
import es.danielmc.notification_websockets.models.Notificacion;
import es.danielmc.titulares.services.TitularesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@CacheConfig(cacheNames = {"dispositivos"})
@RequiredArgsConstructor
public class DispositivosServiceImpl implements DispositivosService, InitializingBean {
    private final DispositivosRepository dispositivosRepository;
    private final DispositivoMapper dispositivoMapper;
    private final TitularesService titularesService;


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

    @CachePut(key = "#result.id")
    @Override
    public DispositivoResponseDto save(DispositivoCreateDto dispositivoCreateDto) {
        log.info("Guardando tarjeta: {}", dispositivoCreateDto);
        // Buscamos el titular por su nombre
        var titular = titularesService.findByNombre(dispositivoCreateDto.getTitular());
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

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando tarjeta por id: {}", id);
        // Si no existe lanza excepción
        Dispositivo dispositivoDeleted = dispositivosRepository.findById(id).orElseThrow(()-> new DispositivoNotFound(id));
        // La borramos del repositorio si existe
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
            senderThread.setName("WebSocketTarjeta-" + data.getId());
            senderThread.setDaemon(true); // Para que no impida que la aplicación se cierre
            senderThread.start();
            log.info("Hilo de websocket iniciado: {}", data.getId());
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }
}