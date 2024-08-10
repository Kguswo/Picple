package com.ssafy.picple.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 변경 요청을 위한 DTO
 * 사용자가 기존, 새 비밀번호 입력 시 사용
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyPasswordRequest {
	private String oldPassword;
	private String newPassword;
}
