package com.ssafy.picple.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "user")
@Getter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 45)
	private String email;

	@Column(nullable = false, length = 200)
	private String password;

	@Column(nullable = false, length = 21)
	private String nickname;

	//	@Column(nullable = false, updatable = false)
	//	private LocalDateTime createdAt = LocalDateTime.now();
	//
	//	@Column(nullable = false)
	//	private LocalDateTime updatedAt = LocalDateTime.now();

	@Column(nullable = false)
	private boolean isDeleted = false;
}