package com.ssafy.picple.domain.chat.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ChatMessage {

	private String content;
	private String sender;
	private MessageType type;

	public enum MessageType {
		CHAT, JOIN, LEAVE
	}

}
