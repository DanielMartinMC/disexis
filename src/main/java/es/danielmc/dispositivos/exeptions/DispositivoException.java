package es.danielmc.dispositivos.exeptions;

public abstract class DispositivoException extends RuntimeException {
    public DispositivoException(String message) {
        super(message);
    }
}