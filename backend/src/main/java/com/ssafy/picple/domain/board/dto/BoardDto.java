package com.ssafy.picple.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
 
	private Long id;
	private String createdAt;
	private String photoUrl;
	private boolean isLiked;
	private int hit;

}
