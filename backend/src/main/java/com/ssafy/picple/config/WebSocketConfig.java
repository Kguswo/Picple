package com.ssafy.picple.config;

import com.ssafy.picple.websocket.WebRTCSignalingHandler;
import com.ssafy.picple.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private final WebRTCSignalingHandler webRTCSignalingHandler;

    public WebSocketConfig(WebRTCSignalingHandler webRTCSignalingHandler) {
        this.webRTCSignalingHandler = webRTCSignalingHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws")
                .setAllowedOrigins("*");
        registry.addHandler(webRTCSignalingHandler, "/signal")
                .setAllowedOrigins("*");
    }
}