package com.ssafy.picple.domain.background.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ssafy.picple.domain.background.entity.Background;

public interface BackgroundRepository extends JpaRepository<Background, Long> {

	// 해당 유저의 삭제되지 않은 배경 사진만
	@Query("SELECT b FROM Background b JOIN BackgroundUser bu ON b.id = bu.background.id WHERE bu.user.id = :userId AND b.isDeleted = false")
	List<Background> findByUserId(@Param("userId") Long userId);

	// 배경 사진 이름 업데이트를 위한 메소드
	@Modifying
	@Query("UPDATE Background b SET b.backgroundTitle = :newName WHERE b.id = :id")
	int updateBackgroundName(@Param("id") Long id, @Param("newName") String newName);

	// 배경 사진 삭제를 위한 메소드
	// 쿼리: userId에 맞는 background 중 backgroundId의 isDeleted를 true로 업데이트
	@Modifying
	@Query("UPDATE Background b SET b.isDeleted = true WHERE b.id = :backgroundId AND b.id IN (SELECT bu.background.id FROM BackgroundUser bu WHERE bu.user.id = :userId)")
	void deleteBackground(@Param("userId") Long userId, @Param("backgroundId") Long backgroundId);

	// 기본 배경 사진을 불러오는 메소드
	List<Background> findByIsDefault(boolean b);
}
