package com.ssafy.picple.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.domain.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

	// 정렬 기준에 따라 게시물 조회
	Page<Board> findAll(Pageable pageable);

	// 사용자 닉네임과 정렬 기준에 따라 게시물 조회
	@Query(value = "SELECT b FROM Board b JOIN b.photo p JOIN PhotoUser pu ON p.id = pu.photo.id JOIN User u ON pu.user.id = u.id WHERE b.isDeleted = false AND u.nickname = :nickname")
	Page<Board> findAllByNickname(String nickname, Pageable pageable);

	// 사진 좋아요 1증가
	@Transactional
	@Modifying
	@Query("UPDATE Board b SET b.hit = b.hit + 1 WHERE b.id = :boardId")
	void increaseHit(@Param("boardId") Long boardId);

	// 사진 좋아요 1감소
	@Transactional
	@Modifying
	@Query("UPDATE Board b SET b.hit = b.hit -1 WHERE b.id = :boardId AND b.hit>0")
	void decreaseHit(@Param("boardId") Long boardId);

	// 내가 올린 게시물 삭제
	@Transactional
	@Modifying
	@Query("UPDATE Board b SET b.isDeleted = true WHERE b.id = :boardId AND b.user.id = :userId")
	int deleteMyBoard(@Param("boardId") Long boardId, @Param("userId") Long userId);

	// 특정 유저가 특정 사진 공유한 게시물 찾기
	Board findByUserIdAndPhotoIdAndIsDeletedFalse(Long userId, Long photoId);
}
