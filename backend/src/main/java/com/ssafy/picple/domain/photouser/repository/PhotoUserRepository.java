package com.ssafy.picple.domain.photouser.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.ssafy.picple.domain.photouser.entity.PhotoUser;

public interface PhotoUserRepository extends JpaRepository<PhotoUser, Long> {

	// 유저 아이디로 그 사람이 찍은 사진의 content찾기
	PhotoUser findByPhotoIdAndUserId(Long photoId, Long userId);

	// photoId와 userId 일치하는 항목 있는지 중복체크(특정 사람과 특정 사진 관계 중복저장 방지)
	boolean existsByPhotoIdAndUserId(Long photoId, Long userId);

	// 유저가 가진 모든 사진 찾기
	List<PhotoUser> findByUserId(@Param("userId") Long userId);

	// 사진에 포함된 모든 유저 찾기
	List<PhotoUser> findByPhotoId(@Param("photoId") Long photoId);

}
