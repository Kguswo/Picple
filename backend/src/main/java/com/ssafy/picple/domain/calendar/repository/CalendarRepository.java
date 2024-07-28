package com.ssafy.picple.domain.calendar.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.picple.domain.calendar.entity.Calendar;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

	// 캘린더 일별 정보 조회
	@Query("SELECT c FROM Calendar c WHERE c.user.id = :userId AND DATE(c.createdAt) = :createdAt AND c.photo.isDeleted = false")
	List<Calendar> findByUserIdAndCreatedAt(@Param("userId") Long userId, @Param("createdAt") LocalDate createdAt);

	// 특정 사용자의 특정 년월에 해당하는 각 날짜별 캘린더 항목 개수 조회 - LocalDateTime -> LocalDate
	@Query(value = "SELECT COUNT(*) FROM Calendar c " +
			"JOIN Photo p ON c.photo_id = p.photo_id " +
			"WHERE c.user_id = :userId AND p.is_deleted = false AND DATE(c.created_at) = :createdAt", nativeQuery = true)
	Long countByUserIdAndDate(@Param("userId") Long userId, @Param("createdAt") LocalDate createdAt);

}
