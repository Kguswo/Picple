package com.ssafy.picple.domain.background.dto.response;

import java.util.List;

import com.ssafy.picple.domain.background.entity.Background;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * 기본 배경에 대한 응답 DTO
 */

@Getter
@SuperBuilder
public class DefaultBackgroundResponse extends BackgroundResponseDto {
	private List<Long> userIds; // 사용자 ID 목록

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
