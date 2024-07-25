package com.ssafy.picple.domain.photouser.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.picple.domain.photouser.entity.PhotoUser;

public interface PhotoUserRepository extends JpaRepository<PhotoUser, Long> {
}
