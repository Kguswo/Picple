package com.ssafy.picple.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebRTCSignalingHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToBooth = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        System.out.println("WebSocket 연결 성공: " + sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        SignalMessage signalMessage = objectMapper.readValue(payload, SignalMessage.class);

        String type = signalMessage.getType();
        String boothId = signalMessage.getBoothId();
        String sessionId = session.getId();

        switch (type) {
            case "join":
                handleJoinBooth(session, boothId);
                break;
            case "offer":
            case "answer":
            case "ice-candidate":
                forwardMessage(boothId, sessionId, payload);
                break;
            case "leave":
                handleLeaveBooth(session);
                break;
            default:
                System.out.println("Unknown message type: " + type);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        handleLeaveBooth(session);
        sessions.remove(session.getId());
        System.out.println("WebSocket 연결 종료: " + session.getId());
    }

    private void handleJoinBooth(WebSocketSession session, String boothId) throws IOException {
        String sessionId = session.getId();
        sessionToBooth.put(sessionId, boothId);

        // 같은 부스에 있는 다른 참가자들에게 새 참가자 알림
        for (Map.Entry<String, String> entry : sessionToBooth.entrySet()) {
            if (entry.getValue().equals(boothId) && !entry.getKey().equals(sessionId)) {
                WebSocketSession peerSession = sessions.get(entry.getKey());
                if (peerSession != null && peerSession.isOpen()) {
                    SignalMessage joinMessage = new SignalMessage("new-peer", boothId, sessionId, null);
                    peerSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(joinMessage)));
                }
            }
        }
    }

    private void handleLeaveBooth(WebSocketSession session) {
        String sessionId = session.getId();
        String boothId = sessionToBooth.remove(sessionId);

        if (boothId != null) {
            // 같은 부스에 있는 다른 참가자들에게 참가자 퇴장 알림
            for (Map.Entry<String, String> entry : sessionToBooth.entrySet()) {
                if (entry.getValue().equals(boothId)) {
                    WebSocketSession peerSession = sessions.get(entry.getKey());
                    if (peerSession != null && peerSession.isOpen()) {
                        try {
                            SignalMessage leaveMessage = new SignalMessage("peer-left", boothId, sessionId, null);
                            peerSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(leaveMessage)));
                        } catch (IOException e) {
                            System.err.println("Error sending peer-left message: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void forwardMessage(String boothId, String senderSessionId, String message) throws IOException {
        for (Map.Entry<String, String> entry : sessionToBooth.entrySet()) {
            if (entry.getValue().equals(boothId) && !entry.getKey().equals(senderSessionId)) {
                WebSocketSession peerSession = sessions.get(entry.getKey());
                if (peerSession != null && peerSession.isOpen()) {
                    peerSession.sendMessage(new TextMessage(message));
                }
            }
        }
    }
}