package com.ssafy.picple.domain.photoUser.repository;

import com.ssafy.picple.domain.photoUser.entity.PhotoUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoUserRepository extends JpaRepository<PhotoUser, Long> {
}
