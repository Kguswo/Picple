package com.ssafy.picple.domain.board.dto;

import com.ssafy.picple.domain.board.entity.Board;
import com.ssafy.picple.domain.boardlike.repository.BoardLikeRepository;
import com.ssafy.picple.domain.photo.repository.PhotoRepository;

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

	public BoardDto(Board board, PhotoRepository photoRepository, BoardLikeRepository boardLikeRepository,
		Long userId) {
		this.id = board.getId();
		this.createdAt = board.getCreatedAt().toString();
		this.photoUrl = photoRepository.findById(board.getPhoto().getId()).get().getPhotoUrl();
		this.isLiked = boardLikeRepository.existsByBoardIdAndUserId(board.getId(), userId);
		this.hit = board.getHit();
	}
}
