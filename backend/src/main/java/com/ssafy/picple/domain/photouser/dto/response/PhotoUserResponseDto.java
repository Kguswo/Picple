package com.ssafy.picple.domain.photouser.dto.response;

import com.ssafy.picple.domain.photouser.entity.PhotoUser;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * PhotoUser에 대한 응답 데이터를 담는 DTO
 * 관련된 응답 정보를 담고 있음
 * 사진 ID와 내용 등을 포함
 */

@Getter
@Setter
@SuperBuilder
public class PhotoUserResponseDto {
	private Long id;
	private Long photoId;
	private String content;

	/**
	 * PhotoUser 엔티티 기반, PhotoUserResponseDto 객체를 생성하는 정적 팩토리 메서드
	 * PhotoUser 객체에서 사진 ID와 내용을 추출, PhotoUserResponseDto 객체를 빌드
	 */
	public static PhotoUserResponseDto photoUserResponseDto(PhotoUser photoUser) {
		return PhotoUserResponseDto.builder()
				.id(photoUser.getId())
				.photoId(photoUser.getPhoto().getId())
				.content(photoUser.getContent())
				.build();
	}
}
