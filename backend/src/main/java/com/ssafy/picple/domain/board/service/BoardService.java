package com.ssafy.picple.domain.board.service;

import java.util.List;

import com.ssafy.picple.domain.board.dto.BoardDto;

public interface BoardService {

	// Board 전체 조회
	List<BoardDto> findAllBoards();

	// 좋아요 내림차순으로 정렬
	List<BoardDto> findAllBoardsOrderByHitDesc();

	// 최신순으로 정렬
	List<BoardDto> findAllBoardsOrderByCreatedAtDesc();

	// 사진 좋아요 여부 확인
	boolean isPhotoLikedByUser(Long boardId, Long userId);

	// 사진 좋아요 누르기
	void incrementBoardHit(Long boardId);

	// 사진 좋아요 취소하기
	void decrementBoardHit(Long boardId);

	// 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회
	List<BoardDto> findAllBoardsByUserNickname(String nickname);

}
