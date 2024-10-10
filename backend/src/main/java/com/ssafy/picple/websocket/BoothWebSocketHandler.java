package com.ssafy.picple.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.picple.domain.chat.entity.ChatMessage;

@Component
public class BoothWebSocketHandler extends TextWebSocketHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Map<String, Map<String, WebSocketSession>> boothSessions = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String boothId = extractBoothId(session);
		boothSessions.computeIfAbsent(boothId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String boothId = extractBoothId(session);
		Map<String, WebSocketSession> sessions = boothSessions.get(boothId);

		if (sessions != null) {
			ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
			String messageJson = objectMapper.writeValueAsString(chatMessage);

			for (WebSocketSession webSocketSession : sessions.values()) {
				if (webSocketSession.isOpen() && !session.getId().equals(webSocketSession.getId())) {
					webSocketSession.sendMessage(new TextMessage(messageJson));
				}
			}
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String boothId = extractBoothId(session);
		Map<String, WebSocketSession> sessions = boothSessions.get(boothId);
		if (sessions != null) {
			sessions.remove(session.getId());
			if (sessions.isEmpty()) {
				boothSessions.remove(boothId);
			}
		}
	}

	private String extractBoothId(WebSocketSession session) {
		String path = session.getUri().getPath();
		return path.substring(path.lastIndexOf('/') + 1);
	}
}