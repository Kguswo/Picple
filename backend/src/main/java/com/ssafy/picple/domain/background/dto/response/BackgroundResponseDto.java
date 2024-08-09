package com.ssafy.picple.domain.background.dto.response;

import com.ssafy.picple.domain.background.entity.Background;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 베이스가 되는 배경 응답 DTO
 */

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BackgroundResponseDto {

	private Long id; // 배경 ID
	private String backgroundTitle; // 배경 제목
	private String imageUrl; // 사진 URL
	private Boolean isDefault; // 기본 배경 여부
	private Boolean isDeleted; // 삭제 여부

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