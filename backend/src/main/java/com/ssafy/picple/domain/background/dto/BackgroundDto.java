package com.ssafy.picple.domain.background.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackgroundDto {
	private Long id;
	private String backgroundTitle;
	private LocalDateTime createdAt;
	private Boolean isDeleted;

	// 기본 배경화면의 경우 isDeleted 필요 없음
	public BackgroundDto(Long id, String backgroundTitle, LocalDateTime createdAt) {
	}
}