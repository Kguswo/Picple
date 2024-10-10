package com.ssafy.picple.domain.background.entity;

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
 * Background 엔티티 클래스
 * 배경 사진과 관련된 정보 저장
 */

@Entity
@Getter
@Table(name = "background")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Background {

	@Id
	@Column(name = "background_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 배경 ID (PK)

	@Column(nullable = false, length = 100)
	private String backgroundTitle; // 배경 제목

	@Column(name = "background_url", nullable = false, length = 500)
	private String backgroundUrl;

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Boolean isDefault; // 기본 배경 여부, 기본값 false

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Boolean isDeleted; // 삭제 여부, 기본값 false

	@Builder
	public Background(String backgroundTitle, String backgroundUrl) {
		this.backgroundTitle = backgroundTitle;
		this.isDefault = false;
		this.isDeleted = false;
		this.backgroundUrl = backgroundUrl.length() > 500 ? backgroundUrl.substring(0, 500) : backgroundUrl;
	}

	// 배경 제목 수정 메서드
	public void modifyTitle(String newTitle) {
		this.backgroundTitle = newTitle;
	}

	// 배경 삭제 메서드, isDeleted 필드를 true로 설정
	public void deleteBackground(Background background) {
		background.isDeleted = true;
	}

}