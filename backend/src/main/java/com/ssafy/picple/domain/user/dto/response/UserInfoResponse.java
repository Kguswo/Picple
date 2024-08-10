package com.ssafy.picple.domain.user.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 정보 응답을 위한 DTO
 * 사용자 계정의 기본 정보를 반환
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
	private String email;
	private String nickname;
	private LocalDate createdAt;
}
