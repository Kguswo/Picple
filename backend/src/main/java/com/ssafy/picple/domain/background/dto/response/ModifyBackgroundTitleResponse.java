package com.ssafy.picple.domain.background.dto.response;

import com.ssafy.picple.domain.background.entity.Background;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ModifyBackgroundTitleResponse extends BackgroundResponseDto {
	public static ModifyBackgroundTitleResponse modifyBackgroundTitleResponse(Background background) {
		return ModifyBackgroundTitleResponse.builder()
				.id(background.getId())
				.backgroundTitle(background.getBackgroundTitle())
				.build();
	}
}