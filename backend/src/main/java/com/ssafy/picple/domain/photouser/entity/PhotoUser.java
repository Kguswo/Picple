package com.ssafy.picple.domain.photouser.entity;

import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "photo_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoUser {

	@Id
	@Column(name = "photo_user_id")
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