package com.ssafy.picple.domain.photouser.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PhotoUserRequestDto {
	private Long photoId;
	private String content;

	public static PhotoUserRequestDto photoUserRequestDto(Long photoId, String content) {
		return PhotoUserRequestDto.builder()
				.photoId(photoId)
				.content(content)
				.build();
	}
}
