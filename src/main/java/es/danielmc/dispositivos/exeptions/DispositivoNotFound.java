package es.danielmc.dispositivos.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DispositivoNotFound extends DispositivoException {
    public DispositivoNotFound(Long id) {
        super("Dispositivo con id " + id + " no encontrado");
    }

    public DispositivoNotFound(UUID uuid) {
        super("Dispositivo con uuid " + uuid + " no encontrado");
    }

}