package es.danielmc.rest.titulares.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class TitularNotFoundException extends TitularException {
    public TitularNotFoundException(Long id) {
        super("Titular con id " + id + " no encontrado");
    }

    public TitularNotFoundException(String titular) {
        super("Titular " + titular + " no encontrado");
    }
}