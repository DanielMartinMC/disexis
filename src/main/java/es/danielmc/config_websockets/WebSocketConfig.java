package es.danielmc.config_websockets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${api.version}")
    private String apiVersion;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketDispositivosHandler(), "/ws/" + apiVersion + "/dispositivos")
                .setAllowedOrigins("*"); // Añade esto si tienes problemas de CORS al probar desde fuera
    }

    @Bean
    public WebSocketHandler webSocketDispositivosHandler() {
        return new WebSocketHandler("Dispositivos");
    }
}