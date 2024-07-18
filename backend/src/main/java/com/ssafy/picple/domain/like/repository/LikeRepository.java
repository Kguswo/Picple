package com.ssafy.picple.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.picple.domain.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

}
