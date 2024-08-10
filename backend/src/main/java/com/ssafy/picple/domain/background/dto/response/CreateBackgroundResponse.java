package com.ssafy.picple.domain.background.dto.response;

import com.ssafy.picple.domain.background.entity.Background;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * 배경 생성에 대한 응답 DTO
 */

@Getter
@SuperBuilder
public class CreateBackgroundResponse extends BackgroundResponseDto {
	public static CreateBackgroundResponse insertBackgroundResponse(Background background) {
		return CreateBackgroundResponse.builder()
				.id(background.getId())
				.backgroundTitle(background.getBackgroundTitle())
				.build();
	}
}