package com.ssafy.picple.domain.boardlike.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.board.entity.Board;
import com.ssafy.picple.domain.board.repository.BoardRepository;
import com.ssafy.picple.domain.boardlike.entity.BoardLike;
import com.ssafy.picple.domain.boardlike.repository.BoardLikeRepository;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardLikeServiceImpl implements BoardLikeService {

	private final BoardLikeRepository boardLikeRepository;
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

	// 좋아요 및 숫자 1 증가
	@Transactional
	@Override
	public void likePhoto(Long boardId, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException(BaseResponseStatus.GET_USER_EMPTY.getMessage()));
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new RuntimeException(BaseResponseStatus.RESPONSE_ERROR.getMessage()));

		if (!boardLikeRepository.existsByBoardIdAndUserId(boardId, userId)) {
			BoardLike like = BoardLike.builder()
				.user(user)
				.board(board)
				.isLiked(true)
				.build();
			boardLikeRepository.save(like);
			boardRepository.increaseHit(boardId);
		}
	}

	// 좋아요 취소 및 숫자 1 감소
	@Override
	public void unlikePhoto(Long boardId, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException(BaseResponseStatus.GET_USER_EMPTY.getMessage()));
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new RuntimeException(BaseResponseStatus.RESPONSE_ERROR.getMessage()));

		if (boardLikeRepository.existsByBoardIdAndUserId(boardId, userId)) {
			boardLikeRepository.deleteByBoardIdAndUserId(boardId, userId);
			boardRepository.decreaseHit(boardId);
		}
	}

	@Override
	public boolean isPhotoLikedByUser(Long boardId, Long userId) {
		return boardLikeRepository.existsByBoardIdAndUserId(boardId, userId);
	}
}
