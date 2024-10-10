package com.ssafy.picple.domain.background.dto.request;

import lombok.Data;

/**
 *  AI 배경 사진 생성 요청 DTO
 */

@Data
public class CreateAIBackgroundRequest {
	private Long userId; // 사용자 ID
	private String prompt; // AI 생성 프롬프트
}
