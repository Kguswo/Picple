package com.ssafy.picple.domain.boardlike.service;

import java.util.Optional;

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

		Optional<BoardLike> existingLike = boardLikeRepository.findByBoardIdAndUserId(boardId, userId);

		if (existingLike.isPresent()) { // 좋아요 여부 존재시
			BoardLike like = existingLike.get();
			if (!like.getIsLiked()) { // 좋아요 false일때만 바꾸기
				BoardLike updatedLike = BoardLike.builder()
						.id(like.getId())
						.user(like.getUser())
						.board(like.getBoard())
						.isLiked(true)
						.build();
				boardLikeRepository.save(updatedLike);
				boardRepository.increaseHit(boardId);
			} else {
				throw new RuntimeException(BaseResponseStatus.ALREADY_LIKED.getMessage());
			}
		} else { // 좋아요 여부 존재하지 않으면 새로 생성
			BoardLike newLike = BoardLike.builder()
					.user(user)
					.board(board)
					.isLiked(true)
					.build();
			boardLikeRepository.save(newLike);
			boardRepository.increaseHit(boardId);
		}
	}

	// 좋아요 취소 및 숫자 1 감소
	@Transactional
	@Override
	public void unlikePhoto(Long boardId, Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException(BaseResponseStatus.GET_USER_EMPTY.getMessage()));
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new RuntimeException(BaseResponseStatus.RESPONSE_ERROR.getMessage()));

		Optional<BoardLike> existingLike = boardLikeRepository.findByBoardIdAndUserId(boardId, userId);

		if (existingLike.isPresent()) { // 좋아요 여부 존재시
			BoardLike like = existingLike.get();
			if (like.getIsLiked()) { // 좋아요 true일때만 바꾸기
				BoardLike updatedLike = BoardLike.builder()
						.id(like.getId())
						.user(like.getUser())
						.board(like.getBoard())
						.isLiked(false)
						.build();
				boardLikeRepository.save(updatedLike);
				boardRepository.decreaseHit(boardId);
			} else {
				throw new RuntimeException(BaseResponseStatus.ALREADY_UNLIKED.getMessage());
			}
		} else { // 좋아요 여부 존재하지 않을시 취소하지 못하기 때문에
			throw new RuntimeException(BaseResponseStatus.GET_LIKE_EMPTY.getMessage());
		}
	}

	@Override
	public boolean isPhotoLikedByUser(Long boardId, Long userId) {
		Optional<BoardLike> boardLike = boardLikeRepository.findByBoardIdAndUserId(boardId, userId);
		return boardLike.map(BoardLike::getIsLiked).orElse(false);
	}
}
