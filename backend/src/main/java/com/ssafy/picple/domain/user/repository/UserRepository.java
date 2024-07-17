package com.ssafy.picple.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.picple.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
