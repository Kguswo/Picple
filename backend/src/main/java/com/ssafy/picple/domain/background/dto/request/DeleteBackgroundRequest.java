package com.ssafy.picple.domain.background.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배경 사진 삭제 요청 DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBackgroundRequest {
	private Long userId; // 사용자 ID

	// 유효성 검사용

	public boolean isValidUserId() {
		return userId != null && 0 < userId;
	}
}