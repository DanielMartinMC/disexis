package es.danielmc.rest.titulares.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TitularConflictException extends TitularException {

    public TitularConflictException(String message) {
        super(message);
    }
}