package com.ssafy.picple.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.picple.domain.chat.entity.ChatMessage;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
}
