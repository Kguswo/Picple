package com.ssafy.picple.domain.background.dto.response;

import com.ssafy.picple.domain.background.entity.Background;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * 배경 삭제에 대한 응답 DTO
 */

@Getter
@SuperBuilder
public class DeleteBackgroundResponse extends BackgroundResponseDto {
	public static DeleteBackgroundResponse deleteBackgroundResponse(Background background) {
		return DeleteBackgroundResponse.builder()
				.id(background.getId())
				.isDeleted(background.getIsDeleted())
				.build();
	}
}