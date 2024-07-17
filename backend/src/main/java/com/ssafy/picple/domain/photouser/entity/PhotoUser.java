package com.ssafy.picple.domain.photouser.entity;

import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "photo_user")
@Getter
public class PhotoUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "photo_id")
	private Photo photo;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(length = 50)
	private String content;

}