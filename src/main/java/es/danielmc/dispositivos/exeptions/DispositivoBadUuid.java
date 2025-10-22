package es.danielmc.dispositivos.exeptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DispositivoBadUuid extends DispositivoException {
    public DispositivoBadUuid(String uuid) {
        super("UUID: " + uuid + " no válido o de formato incorrecto");
    }
}