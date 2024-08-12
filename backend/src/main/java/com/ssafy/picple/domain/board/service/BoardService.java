package com.ssafy.picple.domain.board.service;

import java.util.List;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.domain.board.dto.BoardDto;

import jakarta.servlet.http.HttpServletRequest;

public interface BoardService {

	// Board 전체 조회
	List<BoardDto> findAllBoards(Long userId);

	// 사용자 기준에 따라 정렬
	List<BoardDto> findAllBoardsOrderByMyCriteria(Long userId, String criteria);

	// 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회
	List<BoardDto> findAllBoardsByUserNickname(Long userId, String nickname);

	// 사용자 닉네임 검색 및 선택 정렬 기준으로 해당 유저(닉네임) 포함된 사진 조회
	List<BoardDto> findAllBoardsByUserNickname(Long userId, String nickname, String criteria, boolean sortDirection);

	// 내가 올린 사진 삭제하기
	boolean deleteBoard(Long boardId, Long userId) throws BaseException;

	// 접속한 유저 아이디
	Long getUserId(HttpServletRequest request) throws BaseException;

}