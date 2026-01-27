package es.danielmc.notification_websockets.dto;

public record DispositivoNotificationResponse (
    Long id,
    String marca,
    String modelo,
    String numeroSerie,
    String fabricante,
    String tipo,
    String titular,


    String createdAt,
    String updatedAt,
    String uuid
){}
