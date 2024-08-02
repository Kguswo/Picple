package com.ssafy.picple.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.picple.domain.user.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 중복 체크
    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.isDeleted = true WHERE u.id = :userId")
    int changeStatusOfDeleted(@Param("userId") Long userId);
}
