package com.ssafy.picple.domain.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponseStatus;
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
	 * 게시글 조회 (사용자 닉네임 검색, 정렬 모두 처리 가능)
	 *
	 * @param userId
	 * @param nickname
	 * @param pageable
	 * @return List
	 */
	@Override
	public List<BoardDto> findBoards(Long userId, String nickname, Pageable pageable) {
		if (nickname == null || nickname.isEmpty() || nickname.equals("")) {
			Page<Board> pageResult = boardRepository.findAll(pageable);
			return pageResult.getContent().stream().map(board -> new BoardDto(
					board.getId(),
					board.getCreatedAt().toString(),
					getPhotoUrl(board),
					isLikedByUser(board, userId),
					board.getHit()
			)).collect(Collectors.toList());
		} else {
			Page<Board> pageResult = boardRepository.findAllByNickname(nickname, pageable);
			return pageResult.stream()
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
