package com.ssafy.picple.domain.background.dto.response;

import com.ssafy.picple.domain.background.entity.Background;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * BackgroundResponseDto 클래스
 * Background 엔티티의 데이터를 응답으로 전달하기 위한 DTO
 */

@Data
@AllArgsConstructor
public class BackgroundResponseDto {

	private Long id; // 배경 ID
	private String backgroundTitle; // 배경 제목
	private String backgroundUrl; // URL
	private Boolean isDefault; // 기본 배경 여부
	private Boolean isDeleted; // 삭제 여부

	// Background 엔티티를 DTO로 변환
	public static BackgroundResponseDto backgroundResponseDto(Background background) {
		return new BackgroundResponseDto(
				background.getId(),
				background.getBackgroundTitle(),
				background.getBackgroundUrl(),
				background.getIsDefault(),
				background.getIsDeleted()
		);
	}
}