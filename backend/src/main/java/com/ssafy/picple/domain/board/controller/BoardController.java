package com.ssafy.picple.domain.board.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.domain.board.dto.BoardDto;
import com.ssafy.picple.domain.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

	private final BoardService boardService;

	@GetMapping("")
	public List<BoardDto> findAllBoards() {
		return boardService.findAllBoards();
	}

	// 좋아요 내림차순으로 정렬된 Board 조회
	@GetMapping("/sortedByHit")
	public List<BoardDto> findAllBoardsOrderByHitDesc() {
		return boardService.findAllBoardsOrderByHitDesc();
	}

	// 최신순으로 정렬된 Board 조회
	@GetMapping("/sortedByCreatedAt9")
	public List<BoardDto> findAllBoardsOrderByCreatedAtDesc() {
		return boardService.findAllBoardsOrderByCreatedAtDesc();
	}

	// 사진 좋아요 여부 확인
	@GetMapping("/isliked")
	public boolean isPhotoLikedByUser(@PathVariable Long boardId, @RequestParam Long userId) {
		return boardService.isPhotoLikedByUser(boardId, userId);
	}

	// 사진 좋아요 누르기
	@PatchMapping("/{boardId}/like")
	public void incrementBoardHit(@PathVariable Long boardId) {
		boardService.incrementBoardHit(boardId);
	}

	// 사진 좋아요 취소하기
	@PatchMapping("/{boardId}/like")
	public void decrementBoardHit(@PathVariable Long boardId) {
		boardService.decrementBoardHit(boardId);
	}

	// 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회
	@GetMapping("/user/{nickname}")
	public List<BoardDto> findAllBoardsByUserNickname(@PathVariable String nickname) {
		return boardService.findAllBoardsByUserNickname(nickname);
	}

	// 내가 올린 게시물 삭제 - 아직 미구현
}
