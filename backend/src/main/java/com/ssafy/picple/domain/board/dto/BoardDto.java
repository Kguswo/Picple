package com.ssafy.picple.domain.board.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
	private Long id;
	private String createdAt;
	private String photoUrl;
	private boolean isLiked;
	private int hit;

	public BoardDto(Long id, String createdAt, Optional<String> photoUrl, boolean isLiked, int hit) {
		this.id = id;
		this.createdAt = createdAt;
		this.photoUrl = photoUrl.orElse("");
		this.isLiked = isLiked;
		this.hit = hit;
	}
}
