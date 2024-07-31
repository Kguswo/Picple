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
    private String targetSessionId;
    private Object data;
}
