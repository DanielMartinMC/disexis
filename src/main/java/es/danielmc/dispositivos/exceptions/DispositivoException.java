package es.danielmc.dispositivos.exceptions;

public abstract class DispositivoException extends RuntimeException {
    public DispositivoException(String message) {
        super(message);
    }
}