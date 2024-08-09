package com.ssafy.picple.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 비밀번호 재설정 요청을 위한 DTO
 * 사용자가 이메일과 새 비밀번호 입력 시 사용
 */

@Getter
@AllArgsConstructor
public class ResetPasswordRequest {
	@Email
	private String email;
	private String password;
}
