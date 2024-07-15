package com.ssafy.picple.domain.User.repository;

import com.ssafy.picple.domain.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
