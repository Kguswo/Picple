package com.ssafy.picple.domain.photo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.picple.domain.photo.entity.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

	// 사진 url로 찾기
	Photo findByPhotoUrl(String photoUrl);

}
