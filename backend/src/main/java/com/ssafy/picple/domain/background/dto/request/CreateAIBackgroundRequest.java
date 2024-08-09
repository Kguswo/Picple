package com.ssafy.picple.domain.background.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  AI 배경 사진 생성 요청 DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAIBackgroundRequest {
	private Long userId; // 사용자 ID
	private String prompt; // AI 생성 프롬프트
}
