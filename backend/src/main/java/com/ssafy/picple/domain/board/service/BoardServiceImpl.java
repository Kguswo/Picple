package com.ssafy.picple.domain.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.picple.domain.board.dto.BoardDto;
import com.ssafy.picple.domain.board.repository.BoardRepository;

@Service
public class BoardServiceImpl implements BoardService {

	private final BoardRepository boardRepository;

	public BoardServiceImpl(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}

	// Board 전체 조회(생성일, 사진, 좋아요여부, 좋아요수)
	@Override
	public List<BoardDto> findAllBoards() {
		return boardRepository.findAllBoards();
	}

	// 좋아요 내림차순으로 정렬
	@Override
	public List<BoardDto> findAllBoardsOrderByHitDesc() {
		return boardRepository.findAllByOrderByHitDesc();
	}

	// 최신순으로 정렬
	@Override
	public List<BoardDto> findAllBoardsOrderByCreatedAtDesc() {
		return boardRepository.findAllByOrderByCreatedAtDesc();
	}

	// 사진 좋아요 여부 확인
	@Override
	public boolean isPhotoLikedByUser(Long boardId, Long userId) {
		return boardRepository.isPhotoLikedByUser(boardId, userId);
	}

	// 사진 좋아요 누르기
	@Override
	public void incrementBoardHit(Long boardId) {
		boardRepository.incrementHit(boardId);
	}

	// 사진 좋아요 취소하기
	@Override
	public void decrementBoardHit(Long boardId) {
		boardRepository.decrementHit(boardId);
	}

	// 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회
	@Override
	public List<BoardDto> findAllBoardsByUserNickname(String nickname) {
		return boardRepository.findAllByUserNickname(nickname);
	}
}
