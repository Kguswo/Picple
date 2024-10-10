package com.ssafy.picple.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignalMessage {
    private String type;
    private String boothId;
    private String sender;
    private String recipient;
    private Object data;

    // 추가 생성자
    public SignalMessage(String type, String boothId, String sender, Object data) {
        this.type = type;
        this.boothId = boothId;
        this.sender = sender;
        this.data = data;
    }
}