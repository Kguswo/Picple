package com.ssafy.picple.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class WebRTCSignalingHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToBooth = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        System.out.println("WebRTC 연결 성공: " + sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SignalMessage signalMessage = objectMapper.readValue(message.getPayload(), SignalMessage.class);
        String sessionId = session.getId();

        switch (signalMessage.getType()) {
            case "join":
                handleJoinBooth(session, signalMessage.getBoothId());
                break;
            case "offer":
            case "answer":
            case "ice-candidate":
                forwardMessage(signalMessage.getBoothId(), sessionId, signalMessage);
                break;
            case "leave":
                handleLeaveBooth(session);
                break;
            default:
                System.out.println("Unknown message type: " + signalMessage.getType());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        handleLeaveBooth(session);
        sessions.remove(session.getId());
        System.out.println("WebRTC 연결 종료: " + session.getId() + " 상태: " + status);
        if (!status.equals(CloseStatus.NORMAL)) {
            System.err.println("비정상 종료 발생. 상태: " + status);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("WebRTC 전송 에러: " + session.getId() + " 에러: " + exception.getMessage());
        exception.printStackTrace();
        handleLeaveBooth(session);
    }

    private void handleJoinBooth(WebSocketSession session, String boothId) throws IOException {
        String sessionId = session.getId();
        sessionToBooth.put(sessionId, boothId);

        // 새로운 참가자에게 기존 참가자 목록 전송
        List<String> existingParticipants = sessionToBooth.entrySet().stream()
                .filter(entry -> entry.getValue().equals(boothId) && !entry.getKey().equals(sessionId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        SignalMessage participantListMessage = new SignalMessage("participant-list", boothId, sessionId, existingParticipants);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(participantListMessage)));

        // 같은 부스에 있는 다른 참가자들에게 새 참가자 알림
        for (Map.Entry<String, String> entry : sessionToBooth.entrySet()) {
            if (entry.getValue().equals(boothId) && !entry.getKey().equals(sessionId)) {
                WebSocketSession peerSession = sessions.get(entry.getKey());
                if (peerSession != null && peerSession.isOpen()) {
                    try {
                        SignalMessage joinMessage = new SignalMessage("new-peer", boothId, sessionId, null);
                        peerSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(joinMessage)));
                    } catch (IOException e) {
                        System.err.println("Error sending new-peer message: " + e.getMessage());
                    }
                }
            }
        }
        System.out.println("Booth join 완료: " + sessionId + " -> " + boothId);
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
        System.out.println("Booth leave 완료: " + sessionId + " -> " + boothId);
    }

    private void forwardMessage(String boothId, String senderSessionId, SignalMessage message) throws IOException {
        String recipientSessionId = message.getRecipient();
        WebSocketSession recipientSession = sessions.get(recipientSessionId);
        if (recipientSession != null && recipientSession.isOpen()) {
            message.setSender(senderSessionId);
            recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        }
    }
}