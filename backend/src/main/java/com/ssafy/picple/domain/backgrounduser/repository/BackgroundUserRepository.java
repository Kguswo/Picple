package com.ssafy.picple.domain.backgrounduser.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.picple.domain.backgrounduser.entity.BackgroundUser;

public interface BackgroundUserRepository extends JpaRepository<BackgroundUser, Long> {
}
