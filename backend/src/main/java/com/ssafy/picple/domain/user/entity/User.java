package com.ssafy.picple.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import com.ssafy.picple.domain.backgrounduser.entity.BackgroundUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user")
public class User {

	@Id
	@Column(name = "user_id")
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
	@OneToMany(mappedBy = "user")
	private List<BackgroundUser> backgrounds = new ArrayList<>();

	@Column(nullable = false)
	private boolean isDeleted = false;

	public void insertBackgroundUser(BackgroundUser backgroundUser) {
		backgrounds.add(backgroundUser);
	}

}