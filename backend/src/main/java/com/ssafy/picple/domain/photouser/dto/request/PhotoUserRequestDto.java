package com.ssafy.picple.domain.photouser.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * PhotoUser에 대한 요청 데이터를 담는 DTO
 * 관련된 요청을 처리하기 위해 사용
 * 요청에 포함될 수 있는 사진 ID와 내용 정보를 담고 있음
 */

@Getter
@Setter
@SuperBuilder
public class PhotoUserRequestDto {
	private Long photoId;
	private String content;

	/**
	 * PhotoUserRequestDto 객체를 생성하는 정적 팩토리 메서드
	 * 사진 ID와 내용을 통해 PhotoUserRequestDto 객체를 빌드함
	 */
	public static PhotoUserRequestDto photoUserRequestDto(Long photoId, String content) {
		return PhotoUserRequestDto.builder()
				.photoId(photoId)
				.content(content)
				.build();
	}
}
