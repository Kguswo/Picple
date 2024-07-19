package com.ssafy.picple.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.picple.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    // 중복 체크
    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);
}
