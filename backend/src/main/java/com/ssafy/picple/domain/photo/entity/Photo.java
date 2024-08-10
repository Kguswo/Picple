package com.ssafy.picple.domain.photo.entity;

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
@Table(name = "photo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {

	@Id
	@Column(name = "photo_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 2083)
	private String photoUrl;

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isShared = false;

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isDeleted;

	@Builder
	public Photo(String photoUrl, boolean isShared, boolean isDeleted) {
		this.photoUrl = photoUrl;
		this.isShared = isShared;
		this.isDeleted = isDeleted;
	}

	public void setIsShared(boolean isShared) {
		this.isShared = isShared;
	}

}
