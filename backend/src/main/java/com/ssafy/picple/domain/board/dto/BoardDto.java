package com.ssafy.picple.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
	private Long id;
	private String createdAt;
	private String photoUrl;
	private boolean isLiked;
	private int hit;
}
