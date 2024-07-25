package com.ssafy.picple.domain.board.service;

import java.util.List;

import com.ssafy.picple.domain.board.dto.BoardDto;

public interface BoardService {

	// Board 전체 조회
	List<BoardDto> findAllBoards();

	// 사용자 기준에 따라 정렬
	List<BoardDto> findAllBoardsOrderByMyCriteria(String criteria);

	// 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회
	List<BoardDto> findAllBoardsByUserNickname(String nickname);

	// 내가 올린 사진 삭제하기
	boolean deleteBoard(Long boardId, Long userId);

}
