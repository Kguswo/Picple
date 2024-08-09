package com.ssafy.picple.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

/**
 * 이메일 인증 번호 확인 요청을 위한 DTO
 * 사용자가 이메일과 인증 번호 입력 시 사용
 */

@Getter
@Setter
public class EmailCheckRequest {
	@Email
	private String email;
	private String authNumber;
}
