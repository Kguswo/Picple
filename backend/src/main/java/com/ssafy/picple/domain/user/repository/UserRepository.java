package com.ssafy.picple.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ssafy.picple.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * 이메일 중복 여부를 확인
	 * @param email 확인할 이메일 주소
	 * @return 이메일이 존재하면 true, 그렇지 않으면 false
	 */
	Boolean existsByEmail(String email);

	/**
	 * 닉네임 중복 여부를 확인
	 * @param nickname 확인할 닉네임
	 * @return 닉네임이 존재하면 true, 그렇지 않으면 false
	 */
	Boolean existsByNickname(String nickname);

	/**
	 * 이메일을 통해 사용자 조회
	 * @param email 조회할 이메일 주소
	 * @return 조회된 사용자가 Optional로 반환
	 */
	Optional<User> findByEmail(String email);

	/**
	 * 특정 사용자 계정을 삭제 상태로 변경
	 * @param userId 삭제 상태로 변경할 사용자의 ID
	 * @return 업데이트된 행의 수
	 */
	@Modifying
	@Query("UPDATE User u SET u.isDeleted = true WHERE u.id = :userId")
	int changeStatusOfDeleted(@Param("userId") Long userId);
}
