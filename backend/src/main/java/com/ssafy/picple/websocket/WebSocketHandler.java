package com.ssafy.picple.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.picple.domain.booth.service.BoothService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BoothService boothService;

    private void handleCreateBooth(WebSocketSession session) throws IOException {
        try {
            String boothId = boothService.createBooth("Default Name", 4);
            Map<String, Object> response = new ConcurrentHashMap<>();
            response.put("type", "booth_created");
            response.put("boothId", boothId);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            System.out.println("부스 생성됨: " + boothId);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new ConcurrentHashMap<>();
            errorResponse.put("type", "error");
            errorResponse.put("message", "Failed to create booth: " + e.getMessage());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorResponse)));
            System.err.println("부스 생성 실패: " + e.getMessage());
        }
    }

    private void handleJoinBooth(WebSocketSession session, String boothId) throws IOException {
        try {
            boolean joined = boothService.joinBooth(boothId);
            Map<String, Object> response = new ConcurrentHashMap<>();
            response.put("type", "joined_booth");
            response.put("boothId", boothId);
            response.put("success", joined);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            System.out.println("부스 참여 " + (joined ? "성공" : "실패") + ": " + boothId);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new ConcurrentHashMap<>();
            errorResponse.put("type", "error");
            errorResponse.put("message", "Failed to join booth: " + e.getMessage());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorResponse)));
            System.err.println("부스 참여 실패: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        System.out.println("WebSocket 연결 성공: " + sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            System.out.println("메시지 수신: " + payload);

            Map<String, Object> jsonMessage = objectMapper.readValue(payload, Map.class);
            String messageType = (String) jsonMessage.get("type");

            switch (messageType) {
                case "create_booth":
                    handleCreateBooth(session);
                    break;
                case "join_booth":
                    String boothId = (String) jsonMessage.get("boothId");
                    handleJoinBooth(session, boothId);
                    break;
                default:
                    System.out.println("Unknown message type: " + messageType);
            }
        } catch (Exception e) {
            System.err.println("메시지 처리 중 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        System.out.println("WebSocket 연결 종료: " + session.getId() + " 상태: " + status);
        if (!status.equals(CloseStatus.NORMAL)) {
            System.err.println("비정상 종료 발생. 상태: " + status);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket 전송 에러: " + session.getId() + " 에러: " + exception.getMessage());
        exception.printStackTrace();
    }


}
