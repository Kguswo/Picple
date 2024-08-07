package com.ssafy.picple.domain.board.controller;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.domain.board.dto.BoardDto;
import com.ssafy.picple.domain.board.service.BoardService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

	private final BoardService boardService;

	/**
	 * Board 전체 조회(생성일, 사진, 좋아요여부, 좋아요수)
	 *
	 * @return
	 */
	@GetMapping("")
	public BaseResponse<List<BoardDto>> findAllBoards(HttpServletRequest request) throws BaseException {
		Long userId = boardService.getUserId(request);
		List<BoardDto> boards = boardService.findAllBoards(userId);
		return new BaseResponse<>(boards);
	}

	/**
	 * 사용자 기준으로 정렬된 Board 조회
	 *
	 * @param criteria
	 * @return
	 */
	@GetMapping("/sorted/{criteria}")
	public BaseResponse<List<BoardDto>> findAllByOrderByMyCriteria(HttpServletRequest request,
			@PathVariable String criteria) throws BaseException {
		Long userId = boardService.getUserId(request);
		List<BoardDto> boards = boardService.findAllBoardsOrderByMyCriteria(userId, criteria);
		return new BaseResponse<>(boards);
	}

	/**
	 * 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회
	 *
	 * @param nickname
	 * @return
	 */
	@GetMapping("/user/{nickname}")
	public BaseResponse<List<BoardDto>> findAllBoardsByUserNickname(HttpServletRequest request,
			@PathVariable String nickname) throws BaseException {
		Long userId = boardService.getUserId(request);
		List<BoardDto> boards = boardService.findAllBoardsByUserNickname(userId, nickname);
		return new BaseResponse<>(boards);
	}

	/**
	 * 사용자 닉네임 검색 및 선택 정렬 기준으로 조회
	 *
	 * @param request
	 * @param nickname
	 * @param criteria
	 * @return
	 * @throws BaseException
	 */
	@GetMapping("/sorted")
	public BaseResponse<List<BoardDto>> findAllBoardsByUserNickname(HttpServletRequest request,
			@RequestParam String nickname, @RequestParam(required = false) String criteria,
			@RequestParam(value = "sortDirection", required = false) boolean sortDirection) throws BaseException {
		Long userId = boardService.getUserId(request);
		List<BoardDto> boards = boardService.findAllBoardsByUserNickname(userId, nickname, criteria, sortDirection);
		return new BaseResponse<>(boards);
	}

	/**
	 * 내가 올린 게시물 삭제
	 *
	 * @param request
	 * @param boardId
	 * @return
	 * @throws BaseException
	 */
	@DeleteMapping("/{boardId}")
	public BaseResponse<?> deleteBoard(HttpServletRequest request, @PathVariable Long boardId) throws BaseException {
		Long userId = boardService.getUserId(request);
		boolean isDeleted = boardService.deleteBoard(boardId, userId);
		if (isDeleted) {
			return new BaseResponse<>(SUCCESS);
		} else {
			return new BaseResponse<>(REQUEST_ERROR);
		}
	}

}
