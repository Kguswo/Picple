package com.ssafy.picple.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 닉네임 변경 요청을 위한 DTO
 * 사용자가 닉네임을 변경할 때 사용
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyNicknameRequest {
	private String nickname;
}
