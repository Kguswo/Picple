package com.ssafy.picple.domain.photouser.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ssafy.picple.domain.photouser.entity.PhotoUser;

public interface PhotoUserRepository extends JpaRepository<PhotoUser, Long> {

	// 유저가 가진 모든 사진 찾기
	@Query("SELECT pu FROM PhotoUser pu WHERE pu.user.id = :userId")
	List<PhotoUser> findByUserId(@Param("userId") Long userId);

	// 사진에 포함된 모든 유저 찾기
	@Query("SELECT pu FROM PhotoUser pu WHERE pu.photo.id = :photoId")
	List<PhotoUser> findByPhotoId(@Param("photoId") Long photoId);

	// 유저의 특정 사진 찾기
	@Query("SELECT pu FROM PhotoUser pu WHERE pu.photo.id = :photoId AND pu.user.id = :userId")
	PhotoUser findByPhotoIdAndUserId(@Param("photoId") Long photoId, @Param("userId") Long userId);

	// 사진의 Content 업데이트
	@Modifying
	@Query("UPDATE PhotoUser pu SET pu.content = :newContent WHERE pu.id = :id")
	void updatePhotoUserContent(@Param("id") Long id, @Param("newContent") String newContent);

}