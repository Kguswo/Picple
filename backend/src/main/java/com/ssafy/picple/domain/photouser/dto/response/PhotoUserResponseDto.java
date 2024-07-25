package com.ssafy.picple.domain.photouser.dto.response;

import com.ssafy.picple.domain.photouser.entity.PhotoUser;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PhotoUserResponseDto {
	private Long id;
	private Long photoId;
	private Long userId;
	private String content;

	public static PhotoUserResponseDto photoUserResponseDto(PhotoUser photoUser) {
		return PhotoUserResponseDto.builder()
				.id(photoUser.getId())
				.photoId(photoUser.getPhoto().getId())
				.userId(photoUser.getUser().getId())
				.content(photoUser.getContent())
				.build();
	}
}
