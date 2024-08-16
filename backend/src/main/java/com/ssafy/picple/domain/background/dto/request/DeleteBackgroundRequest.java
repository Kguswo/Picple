package com.ssafy.picple.domain.background.dto.request;

import lombok.Data;

/**
 * 배경 사진 삭제 요청 DTO
 */

@Data
public class DeleteBackgroundRequest {
	private Long userId; // 사용자 ID

	// 유효성 검사용

	public boolean isValidUserId() {
		return userId != null && 0 < userId;
	}
}