package com.ssafy.picple.domain.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ssafy.picple.domain.board.dto.BoardDto;
import com.ssafy.picple.domain.board.entity.Board;
import com.ssafy.picple.domain.board.repository.BoardRepository;
import com.ssafy.picple.domain.boardlike.repository.BoardLikeRepository;
import com.ssafy.picple.domain.photo.repository.PhotoRepository;
import com.ssafy.picple.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final BoardRepository boardRepository;
	private final PhotoRepository photoRepository;
	private final BoardLikeRepository boardLikeRepository;
	private final UserRepository userRepository;

	// private BoardDto getBoardDto(Board board) {
	// 	return new BoardDto(
	// 		board.getId(),
	// 		board.getCreatedAt().toString(),
	// 		getPhotoUrl(board),
	// 		isLikedByUser(board),
	// 		board.getHit()
	// 	);
	// }

	// Board 전체 조회(생성일, 사진, 좋아요여부, 좋아요수)
	@Override
	public List<BoardDto> findAllBoards() {
		Long userId = getUserId();
		List<Board> boards = boardRepository.findAllByIsDeletedFalse();
		return boards.stream()
			.map(board -> new BoardDto(board, photoRepository, boardLikeRepository, userId))
			.collect(Collectors.toList());
	}

	// 좋아요 여부 표시위함
	private boolean isLikedByUser(Board board) {
		Long userId = getUserId();
		return boardLikeRepository.existsByBoardIdAndUserId(board.getId(), userId);
	}

	// 사진 표시 위함
	private String getPhotoUrl(Board board) {
		// Photo photo = photoRepository.findById(board.getPhoto().getId()).get();
		// return photo.getPhotoUrl();
		return photoRepository.findById(board.getPhoto().getId()).get().getPhotoUrl();
	}

	// 좋아요 내림차순으로 정렬
	@Override
	public List<BoardDto> findAllBoardsOrderByHitDesc() {
		Long userId = getUserId();
		List<Board> boards = boardRepository.findAllByOrderByHitDesc();
		return boards.stream()
			.map(board -> new BoardDto(board, photoRepository, boardLikeRepository, userId))
			.collect(Collectors.toList());
	}

	// 최신순으로 정렬
	@Override
	public List<BoardDto> findAllBoardsOrderByCreatedAtDesc() {
		Long userId = getUserId();
		List<Board> boards = boardRepository.findAllByOrderByCreatedAtDesc();
		return boards.stream()
			.map(board -> new BoardDto(board, photoRepository, boardLikeRepository, userId))
			.collect(Collectors.toList());
	}

	// 사진 좋아요 1증가
	@Override
	public void increaseBoardHit(Long boardId) {
		boardRepository.increaseHit(boardId);
	}

	// 사진 좋아요 1감소
	@Override
	public void decreaseBoardHit(Long boardId) {
		boardRepository.decreaseHit(boardId);
	}

	// 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회
	@Override
	public List<BoardDto> findAllBoardsByUserNickname(String nickname) {
		Long userId = getUserId();
		List<Board> boards = boardRepository.findAllByUserNickname(nickname);
		return boards.stream()
			.map(board -> new BoardDto(board, photoRepository, boardLikeRepository, userId))
			.collect(Collectors.toList());
	}

	// 내가 올린 사진 삭제
	@Override
	public boolean deleteBoard(Long boardId, Long userId) {
		int deletedCount = boardRepository.deleteMyBoard(boardId, userId);
		return deletedCount > 0;
	}

	// 현재 사용자의 ID, 임시로 반환중 수정해야함
	private Long getUserId() {
		// Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// String userEmail = authentication.getName();
		// return userRepository.findByEmail(userEmail).getId();
		return 1L;
	}

}
