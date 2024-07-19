package com.ssafy.picple.domain.photo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

	@Column(nullable = false)
	private boolean isShared = false;

	@Column(nullable = false)
	private boolean isDeleted;

}
