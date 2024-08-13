package com.ssafy.picple.domain.board.service;

import java.util.List;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.domain.board.dto.BoardDto;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

public interface BoardService {

	// 게시글 조회 (사용자 닉네임 검색, 정렬 모두 처리 가능)
	List<BoardDto> findBoards(Long userId, String nickname, Pageable pageable);

	// 내가 올린 사진 삭제하기
	boolean deleteBoard(Long boardId, Long userId) throws BaseException;

	// 접속한 유저 아이디
	Long getUserId(HttpServletRequest request) throws BaseException;
}