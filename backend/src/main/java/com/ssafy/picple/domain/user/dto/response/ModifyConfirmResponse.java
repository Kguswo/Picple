package com.ssafy.picple.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 닉네임 수정 확인 응답을 위한 DTO
 * 닉네임 수정 요청 후 수정된 이메일과 닉네임을 반환
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyConfirmResponse {
	private String email;
	private String nickname;
}
