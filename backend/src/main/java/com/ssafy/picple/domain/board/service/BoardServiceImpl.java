package com.ssafy.picple.domain.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.board.dto.BoardDto;
import com.ssafy.picple.domain.board.entity.Board;
import com.ssafy.picple.domain.board.repository.BoardRepository;
import com.ssafy.picple.domain.boardlike.entity.BoardLike;
import com.ssafy.picple.domain.boardlike.repository.BoardLikeRepository;
import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.photo.repository.PhotoRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final BoardRepository boardRepository;
	private final PhotoRepository photoRepository;
	private final BoardLikeRepository boardLikeRepository;

	/**
	 * Board 전체 조회(생성일, 사진, 좋아요여부, 좋아요수) - 기본 최신순(내림차순)
	 *
	 * @return
	 */
	@Override
	public List<BoardDto> findAllBoards(Long userId) {
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		List<Board> boards = boardRepository.findByIsDeletedFalse(sort);
		return boards.stream()
				.map(board -> new BoardDto(
						board.getId(),
						board.getCreatedAt().toString(),
						getPhotoUrl(board),
						isLikedByUser(board, userId),
						board.getHit()
				))
				.collect(Collectors.toList());
	}

	// 좋아요 여부 표시위함
	private boolean isLikedByUser(Board board, Long userId) {
		Long boardId = board.getId();
		return boardLikeRepository.findByBoardIdAndUserId(boardId, userId)
				.map(BoardLike::getIsLiked)
				.orElse(false);
	}

	// 사진 표시 위함
	private String getPhotoUrl(Board board) {
		return photoRepository.findById(board.getPhoto().getId()).get().getPhotoUrl();
	}

	/**
	 * 사용자 기준에 따라 정렬(전체 게시물)
	 *
	 * @param userId
	 * @param criteria
	 * @return
	 */
	@Override
	public List<BoardDto> findAllBoardsOrderByMyCriteria(Long userId, String criteria) {
		Sort sort = Sort.by(Sort.Direction.DESC, criteria);
		List<Board> boards = boardRepository.findAll(sort);
		return boards.stream()
				.filter(board -> !board.isDeleted())
				.map(board -> new BoardDto(
						board.getId(),
						board.getCreatedAt().toString(),
						getPhotoUrl(board),
						isLikedByUser(board, userId),
						board.getHit()
				))
				.collect(Collectors.toList());
	}

	/**
	 * 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회(기본정렬)
	 *
	 * @param userId
	 * @param nickname
	 * @return
	 */
	@Override
	public List<BoardDto> findAllBoardsByUserNickname(Long userId, String nickname) {
		List<Board> boards = boardRepository.findAllByUserNickname(nickname);
		return boards.stream()
				.map(board -> new BoardDto(
						board.getId(),
						board.getCreatedAt().toString(),
						getPhotoUrl(board),
						isLikedByUser(board, userId),
						board.getHit()
				))
				.collect(Collectors.toList());
	}

	/**
	 * 자율적으로 사용자 닉네임 검색 및 선택 정렬 기준으로 조회
	 *
	 * @param userId
	 * @param nickname
	 * @param criteria
	 * @return
	 */
	@Override
	public List<BoardDto> findAllBoardsByUserNickname(Long userId, String nickname, String criteria,
			boolean direction) {
		// 정렬기준의 존재여부에 따라 적용할 정렬기준 선택 - 최신순, 좋아요순, 기본순 + 정렬 방향 false 내림차순, true 오름차순
		Sort.Direction sortDirection = direction ? Sort.Direction.ASC : Sort.Direction.DESC;
		Sort sort = (criteria != null && !criteria.isEmpty()) ? Sort.by(sortDirection, criteria) : Sort.unsorted();

		if (nickname == null || nickname.isEmpty() || nickname.equals("")) {
			List<Board> boards = boardRepository.findByIsDeletedFalse(sort);
			return boards.stream()
					.filter(board -> !board.isDeleted())
					.map(board -> new BoardDto(
							board.getId(),
							board.getCreatedAt().toString(),
							getPhotoUrl(board),
							isLikedByUser(board, userId),
							board.getHit()
					))
					.collect(Collectors.toList());
		} else {
			// 정렬 기준 체크 후 그에 해당하는 정렬 실행
			List<Board> boards = sort.isUnsorted() ?
					boardRepository.findAllByUserNickname(nickname) :
					boardRepository.findAllByUserNickname(nickname, sort);

			return boards.stream()
					.filter(board -> !board.isDeleted())
					.map(board -> new BoardDto(
							board.getId(),
							board.getCreatedAt().toString(),
							getPhotoUrl(board),
							isLikedByUser(board, userId),
							board.getHit()
					))
					.collect(Collectors.toList());
		}

	}

	/**
	 * 내가 올린 사진 삭제
	 * board에서 삭제 후 photo에서 isShared도 false로 바꾼다
	 *
	 * @param boardId
	 * @param userId
	 * @return
	 */
	@Override
	public boolean deleteBoard(Long boardId, Long userId) throws BaseException {
		int deletedCount = boardRepository.deleteMyBoard(boardId, userId);
		if (deletedCount > 0) {
			Board board = boardRepository.findById(boardId)
					.orElseThrow(() -> new BaseException(BaseResponseStatus.GET_BOARD_EMPTY));
			Photo photo = board.getPhoto();
			photo.setIsShared(false);
			photoRepository.save(photo);
			return true;
		}
		return false;
	}

	// 자주사용되는 로그인유저아이디 가져오기 메서드화
	public Long getUserId(HttpServletRequest request) throws BaseException {
		return (Long)request.getAttribute("userId");
	}

}
