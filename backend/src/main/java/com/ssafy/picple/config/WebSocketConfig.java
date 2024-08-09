package com.ssafy.picple.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.ssafy.picple.websocket.BoothWebSocketHandler;
import com.ssafy.picple.websocket.WebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private WebSocketHandler webSocketHandler;
	@Autowired
	private BoothWebSocketHandler boothWebSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketHandler, "/ws")
				.setAllowedOrigins("*");

		// 부스별 웹소켓 엔드포인트
		registry.addHandler(boothWebSocketHandler, "/booth")
				.setAllowedOrigins("*");
	}
}