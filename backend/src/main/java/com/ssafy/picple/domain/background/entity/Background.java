package com.ssafy.picple.domain.background.entity;

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

@Entity
@Getter
@Table(name = "background")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Background extends BaseTimeEntity {

	@Id
	@Column(name = "background_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 45)
	private String backgroundTitle;

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Boolean isDefault;

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Boolean isDeleted;

	@Column(nullable = false)
	private String backgroundUrl;

	@Builder
	public Background(String backgroundTitle, String backgroundUrl) {
		this.backgroundTitle = backgroundTitle;
		this.isDefault = false;
		this.isDeleted = false;
		this.backgroundUrl = backgroundUrl;
	}

	public void modifyTitle(String newTitle) {
		this.backgroundTitle = newTitle;
	}

	public void deleteBackground(Background background) {
		background.isDeleted = true;
	}

}