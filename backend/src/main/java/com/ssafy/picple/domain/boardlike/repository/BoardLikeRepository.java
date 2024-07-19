package com.ssafy.picple.domain.boardlike.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.picple.domain.boardlike.entity.BoardLike;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

	// // 사진 좋아요 1증가
	// @Transactional
	// @Modifying
	// @Query("UPDATE Board b SET b.hit = b.hit + 1 WHERE b.id = :boardId")
	// void increaseHit(@Param("boardId") Long boardId);
	//
	// // 사진 좋아요 1감소
	// @Transactional
	// @Modifying
	// @Query("UPDATE Board b SET b.hit = b.hit -1 WHERE b.id = :boardId AND b.hit>0")
	// void decreaseHit(@Param("boardId") Long boardId);

	// 좋아요 조회
	boolean existsByBoardIdAndUserId(Long boardId, Long userId);

	// 좋아요 취소
	void deleteByBoardIdAndUserId(Long boardId, Long userId);

}
