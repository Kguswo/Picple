package com.ssafy.picple.domain.boardlike.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.picple.domain.boardlike.entity.BoardLike;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

	// 좋아요 조회 - boardlike테이블에 존재 여부
	boolean existsByBoardIdAndUserId(Long boardId, Long userId);

	// 좋아요 상태값 조회 - boardlike테이블에 존재시 isLiked값
	Optional<BoardLike> findByBoardIdAndUserId(Long boardId, Long userId);

}
