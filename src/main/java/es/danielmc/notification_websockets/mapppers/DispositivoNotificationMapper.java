package es.danielmc.notification_websockets.mapppers;

import es.danielmc.dispositivos.models.Dispositivo;
import es.danielmc.notification_websockets.dto.DispositivoNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class DispositivoNotificationMapper {

    public DispositivoNotificationResponse toDispositivoNotificationDto(Dispositivo dispositivo) {
        return new DispositivoNotificationResponse(
                dispositivo.getId(),
                dispositivo.getMarca(),
                dispositivo.getModelo(),
                dispositivo.getNumeroSerie(),
                dispositivo.getTitular().getNombre(),
                dispositivo.getFabricante(),
                dispositivo.getTipo(),
                dispositivo.getCreatedAt().toString(),
                dispositivo.getUpdatedAt().toString(),
                dispositivo.getUuid().toString()
        );
    }
}
