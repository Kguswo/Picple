package com.ssafy.picple.domain.backgrounduser.entity;

import com.ssafy.picple.domain.background.entity.Background;
import com.ssafy.picple.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Background와 User간의 관계를 나타내는 엔티티 클래스
 * 특정 배경 사진이 특정 사용자와 연결되어 있음을 나타냄
 * 지연 로딩 방식으로 설정, 실제로 사용될 때 DB에서 조회됨
 */

@Entity
@Getter
@NoArgsConstructor
@Table(name = "background_user")
public class BackgroundUser {

	@Id
	@Column(name = "background_user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 고유 식별자

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "background_id", nullable = false)
	private Background background; // 연결된 배경 사진을 참조

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // 연결된 사용자를 참조

	// Background와 User간의 관계를 생성
	@Builder
	public BackgroundUser(Background background, User user) {
		this.background = background;
		this.user = user;
	}

}
