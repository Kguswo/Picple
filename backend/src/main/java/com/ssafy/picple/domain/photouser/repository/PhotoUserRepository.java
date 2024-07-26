package com.ssafy.picple.domain.photouser.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.picple.domain.photouser.entity.PhotoUser;

public interface PhotoUserRepository extends JpaRepository<PhotoUser, Long> {

	// 유저 아이디로 그 사람이 찍은 사진의 content찾기
	PhotoUser findByPhotoIdAndUserId(Long photoId, Long userId);

}
