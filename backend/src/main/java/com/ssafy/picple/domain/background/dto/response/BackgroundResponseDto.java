package com.ssafy.picple.domain.background.dto.response;

import com.ssafy.picple.domain.background.entity.Background;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BackgroundResponseDto {

	private Long id;
	private String backgroundTitle;
	private String imageUrl;
	private Boolean isDefault;
	private Boolean isDeleted;

	public static BackgroundResponseDto backgroundResponseDto(Background background) {
		return BackgroundResponseDto.builder()
				.id(background.getId())
				.backgroundTitle(background.getBackgroundTitle())
				.imageUrl(background.getBackgroundUrl())
				.isDefault(background.getIsDefault())
				.isDeleted(background.getIsDeleted())
				.build();
	}
}