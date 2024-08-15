package com.ssafy.picple.domain.background.dto.response;

import java.util.List;

import lombok.Data;

/**
 * 기본 배경에 대한 응답 DTO
 */

@Data
public class DefaultBackgroundResponse {
	private List<Long> userIds; // 사용자 ID 목록
}
