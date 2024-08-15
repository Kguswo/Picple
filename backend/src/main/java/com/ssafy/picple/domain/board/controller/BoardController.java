package com.ssafy.picple.domain.board.controller;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponse;
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
	 * 닉네임 검색 및 정렬 기준으로 조회
	 *
	 * @param request
	 * @param nickname
	 * @param pageable
	 * @return BaseResponse
	 * @throws BaseException
	 */
	@GetMapping("")
	public BaseResponse<List<BoardDto>> findBoards(HttpServletRequest request,
																	@RequestParam String nickname, @PageableDefault(page=0, size=20, sort="createdAt", direction=Sort.Direction.DESC) Pageable pageable) throws BaseException {
		Long userId = boardService.getUserId(request);
		List<BoardDto> boards = boardService.findBoards(userId, nickname, pageable);
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
