package com.ssafy.picple.domain.background.dto.response;

import com.ssafy.picple.domain.background.entity.Background;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class InsertBackgroundResponse extends BackgroundResponseDto {
	public static InsertBackgroundResponse insertBackgroundResponse(Background background) {
		return InsertBackgroundResponse.builder()
				.id(background.getId())
				.backgroundTitle(background.getBackgroundTitle())
				.build();
	}
}