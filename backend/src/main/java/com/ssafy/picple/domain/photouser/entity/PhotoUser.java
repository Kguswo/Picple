package com.ssafy.picple.domain.photouser.entity;

import com.ssafy.picple.domain.photo.entity.Photo;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "photo_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoUser {

	@Id
	@Column(name = "photo_user")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "photo_id")
	private Photo photo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(length = 50)
	private String content;

	@Builder
	public PhotoUser(Photo photo, User user, String content) {
		this.photo = photo;
		this.user = user;
		this.content = content;
	}

}