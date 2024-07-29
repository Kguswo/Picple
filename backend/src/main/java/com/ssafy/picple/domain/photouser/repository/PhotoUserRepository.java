package com.ssafy.picple.domain.photouser.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.picple.domain.photouser.entity.PhotoUser;

public interface PhotoUserRepository extends JpaRepository<PhotoUser, Long> {

	// 유저 아이디로 그 사람이 찍은 사진의 content찾기
	PhotoUser findByPhotoIdAndUserId(Long photoId, Long userId);

	// photoId와 userId 일치하는 항목 있는지 중복체크(특정 사람과 특정 사진 관계 중복저장 방지)
    boolean existsByPhotoIdAndUserId(Long photoId, Long userId);
}
