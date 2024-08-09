package com.ssafy.picple.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청을 위한 DTO
 * 사용자가 이메일과 비밀번호로 로그인 시 사용
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
	private String email;
	private String password;
}
