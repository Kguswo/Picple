package com.ssafy.picple.domain.background.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBackgroundRequest {
	private Long userId;

	// 유효성 검사용

	public boolean isValidUserId() {
		return userId != null && 0 < userId;
	}
}