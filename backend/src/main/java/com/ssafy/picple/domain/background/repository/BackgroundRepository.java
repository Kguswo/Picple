package com.ssafy.picple.domain.background.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.picple.domain.background.entity.Background;

@Repository
public interface BackgroundRepository extends JpaRepository<Background, Long> {

	// 해당 유저의 삭제되지 않은 배경 사진만
	@Query("SELECT b FROM Background b JOIN BackgroundUser bu ON b.id = bu.background.id WHERE bu.user.id = :userId AND b.isDeleted = false")
	List<Background> findByUserId(@Param("userId") Long userId);

}
