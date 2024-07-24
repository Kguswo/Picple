package com.ssafy.picple.domain.board.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.board.dto.BoardDto;
import com.ssafy.picple.domain.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

	private final BoardService boardService;

	@GetMapping("")
	public BaseResponse<List<BoardDto>> findAllBoards() {
		List<BoardDto> boards = boardService.findAllBoards();
		return new BaseResponse<>(boards);
	}

	// // 좋아요 내림차순으로 정렬된 Board 조회
	// @GetMapping("/hit")
	// public BaseResponse<List<BoardDto>> findAllBoardsOrderByHitDesc() {
	// 	List<BoardDto> boards = boardService.findAllBoardsOrderByHitDesc();
	// 	return new BaseResponse<>(boards);
	// }

	// // 최신순으로 정렬된 Board 조회
	// @GetMapping("/createdat")
	// public BaseResponse<List<BoardDto>> findAllBoardsOrderByCreatedAtDesc() {
	// 	List<BoardDto> boards = boardService.findAllBoardsOrderByCreatedAtDesc();
	// 	return new BaseResponse<>(boards);
	// }

	// 사용자 기준으로 정렬된 Board 조회
	@GetMapping("/sorted/{criteria}")
	public BaseResponse<List<BoardDto>> findAllByOrderByMyCriteria(@PathVariable String criteria) {
		List<BoardDto> boards = boardService.findAllBoardsOrderByMyCriteria(criteria);
		return new BaseResponse<>(boards);
	}

	// 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회
	@GetMapping("/user/{nickname}")
	public BaseResponse<List<BoardDto>> findAllBoardsByUserNickname(@PathVariable String nickname) {
		List<BoardDto> boards = boardService.findAllBoardsByUserNickname(nickname);
		return new BaseResponse<>(boards);
	}

	// 내가 올린 게시물 삭제
	@DeleteMapping("")
	public BaseResponse<?> deleteBoard(@RequestParam Long boardId, @RequestParam Long userId) {
		boolean isDeleted = boardService.deleteBoard(boardId, userId);
		if (isDeleted) {
			return new BaseResponse<>(BaseResponseStatus.SUCCESS);
		} else {
			return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
		}
	}

}
