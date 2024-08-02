package com.ssafy.picple.domain.background.dto.response;

import java.util.List;

import com.ssafy.picple.domain.background.entity.Background;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DefaultBackgroundResponse extends BackgroundResponseDto {
	private List<Long> userIds;

	public static DefaultBackgroundResponse defaultBackgroundResponse(Background background) {
		return DefaultBackgroundResponse.builder()
				.id(background.getId())
				.backgroundTitle(background.getBackgroundTitle())
				.imageUrl(background.getBackgroundUrl())
				.isDefault(background.getIsDefault())
				.isDeleted(background.getIsDeleted())
				.build();
	}
}
