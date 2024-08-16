package com.ssafy.picple.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

/**
 * 이메일 요청을 위한 DTO
 * 사용자가 이메일 입력 시 사용
 */

@Getter
@Setter
public class EmailRequest {
	@Email(message = "올바른 형식의 이메일을 입력해주세요.")
	private String email;
}
