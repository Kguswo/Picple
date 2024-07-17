package com.ssafy.picple.domain.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.domain.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

	// Board 전체 조회
	List<Board> findAll();

	// 좋아요 내림차순으로 정렬
	List<Board> findAllByOrderByHitDesc();

	// 최신순으로 정렬
	List<Board> findAllByOrderByCreatedAtDesc();

	// 사진 좋아요 여부 확인

	// 사진 좋아요 누르기
	@Transactional
	@Modifying
	@Query("UPDATE Board b SET b.hit = b.hit + 1 WHERE b.id = :boardId")
	void incrementHit(@Param("boardId") Long boardId);

	// 사진 좋아요 취소하기
	@Transactional
	@Modifying
	@Query("UPDATE Board b SET b.hit = b.hit -1 WHERE b.id = :boardID AND b.hit>0")
	void decrementHit(@Param("boardId") Long boardId);

	// 사용자 닉네임 검색으로 해당 유저(닉네임) 포함된 사진 조회
	
}
