package com.ssafy.picple.domain.background.dto.response;

import lombok.Data;

/**
 * 배경 생성에 대한 응답 DTO
 */

@Data
public class CreateBackgroundResponse {
	private Long id;
	private String backgroundTitle;
}