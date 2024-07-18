package com.ssafy.picple.domain.board.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

	// 좋아요 내림차순으로 정렬된 Board 조회
	@GetMapping("/sortedByHit")
	public BaseResponse<List<BoardDto>> findAllBoardsOrderByHitDesc() {
		List<BoardDto> boards = boardService.findAllBoardsOrderByHitDesc();
		return new BaseResponse<>(boards);
	}

	// 최신순으로 정렬된 Board 조회
	@GetMapping("/sortedByCreatedAt")
	public BaseResponse<List<BoardDto>> findAllBoardsOrderByCreatedAtDesc() {
		List<BoardDto> boards = boardService.findAllBoardsOrderByCreatedAtDesc();
		return new BaseResponse<>(boards);
	}

	// 사진 좋아요 여부 확인
	@GetMapping("/isLiked")
	public BaseResponse<?> isPhotoLikedByUser(@RequestParam Long boardId, @RequestParam Long userId) {
		boolean isLiked = boardService.isPhotoLikedByUser(boardId, userId);
		return new BaseResponse<>(isLiked);
	}

	// 사진 좋아요 누르기
	@PatchMapping("/like/{boardId}")
	public BaseResponse<?> increaseBoardHit(@PathVariable Long boardId) {
		boardService.increaseBoardHit(boardId);
		return new BaseResponse<>(BaseResponseStatus.SUCCESS);
	}

	// 사진 좋아요 취소하기
	@PatchMapping("/likeCancel/{boardId}")
	public BaseResponse<?> decreaseBoardHit(@PathVariable Long boardId) {
		boardService.decreaseBoardHit(boardId);
		return new BaseResponse<>(BaseResponseStatus.SUCCESS);
	}

	// 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회
	@GetMapping("/user/{nickname}")
	public BaseResponse<List<BoardDto>> findAllBoardsByUserNickname(@PathVariable String nickname) {
		List<BoardDto> boards = boardService.findAllBoardsByUserNickname(nickname);
		return new BaseResponse<>(boards);
	}

	// 내가 올린 게시물 삭제
	public BaseResponse<?> deleteBoard(@RequestParam Long boardId, @RequestParam Long userId) {
		boolean isDeleted = boardService.deleteBoard(boardId, userId);
		if (isDeleted) {
			return new BaseResponse<>(BaseResponseStatus.SUCCESS);
		} else {
			return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR); // 적절한 실패 상태를 반환하도록 수정하세요.
		}
	}

}
