package com.ssafy.picple.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        System.out.println("WebSocket 연결 성공: " + sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> jsonMessage = objectMapper.readValue(payload, Map.class);
        String messageType = (String) jsonMessage.get("type");

        switch(messageType) {
            case "create_booth":
                handleCreateBooth(session);
                break;
            default:
                System.out.println("Unknown message type: " + messageType);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        System.out.println("WebSocket 연결 종료: " + session.getId());
    }

    private void handleCreateBooth(WebSocketSession session) throws IOException {
        String boothId = UUID.randomUUID().toString();
        Map<String, Object> response = new ConcurrentHashMap<>();
        response.put("type", "booth_created");
        response.put("boothId", boothId);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
}