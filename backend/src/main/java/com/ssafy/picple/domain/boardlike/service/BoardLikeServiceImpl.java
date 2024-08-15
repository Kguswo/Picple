package com.ssafy.picple.domain.boardlike.service;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.domain.board.entity.Board;
import com.ssafy.picple.domain.board.repository.BoardRepository;
import com.ssafy.picple.domain.boardlike.entity.BoardLike;
import com.ssafy.picple.domain.boardlike.repository.BoardLikeRepository;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardLikeServiceImpl implements BoardLikeService {

	private final BoardLikeRepository boardLikeRepository;
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

	// 좋아요 및 숫자 1 증가
	@Transactional
	@Override
	public void likePhoto(Long boardId, Long userId) throws BaseException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BaseException(GET_USER_EMPTY));
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new BaseException(RESPONSE_ERROR));

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
				throw new BaseException(ALREADY_LIKED);
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
	public void unlikePhoto(Long boardId, Long userId) throws BaseException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BaseException(GET_USER_EMPTY));
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new BaseException(RESPONSE_ERROR));

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
				throw new BaseException(ALREADY_UNLIKED);
			}
		} else { // 좋아요 여부 존재하지 않을시 취소하지 못하기 때문에
			throw new BaseException(GET_LIKE_EMPTY);
		}
	}

	@Transactional
	@Override
	public void toggleLike(Long boardId, Long userId) throws BaseException {
		boolean isLiked = isPhotoLikedByUser(boardId, userId);
		if (isLiked) {
			unlikePhoto(boardId, userId);
		} else {
			likePhoto(boardId, userId);
		}
	}

	@Override
	public boolean isPhotoLikedByUser(Long boardId, Long userId) {
		Optional<BoardLike> boardLike = boardLikeRepository.findByBoardIdAndUserId(boardId, userId);
		return boardLike.map(BoardLike::getIsLiked).orElse(false);
	}
}
