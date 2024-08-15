package com.ssafy.picple.domain.user.entity;

import com.ssafy.picple.config.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * User 엔티티 클래스
 * 데이터베이스의 'user' 테이블과 매핑되며, 사용자 정보 관리
 * BaseTimeEntity를 상속받아 생성 및 수정 시간 자동 관리
 */

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id; // 사용자 ID (PK)

	@Column(nullable = false, length = 45)
	private String email; // 사용자 이메일 주소

	@Column(nullable = false, length = 200)
	private String password; // 사용자 비밀번호, 암호화된 상태로 저장

	@Column(nullable = false, length = 21)
	private String nickname; // 사용자 닉네임

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isDeleted;

	@Column(length = 512)
	private String refreshToken;

	@Builder
	public User(String email, String password, String nickname, boolean isDeleted) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.isDeleted = isDeleted;
	}

	// 비밀번호를 암호화된 상태로 설정
	public void setPasswordEncoding(String password) {
		this.password = password;
	}

	// 사용자 닉네임 수정
	public void modifyUserNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setRefreshTokenByLogin(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void deleteRefreshTokenByLogout() {
		this.refreshToken = null;
	}
}