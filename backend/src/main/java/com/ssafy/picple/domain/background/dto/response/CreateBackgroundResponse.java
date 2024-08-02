package com.ssafy.picple.domain.background.dto.response;

import com.ssafy.picple.domain.background.entity.Background;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

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