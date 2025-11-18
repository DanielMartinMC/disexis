package es.danielmc.rest.dispositivos.exeptions;


public class DispositivoBadRequest extends DispositivoException {
    public DispositivoBadRequest(String message) {
        super(message);
    }
}