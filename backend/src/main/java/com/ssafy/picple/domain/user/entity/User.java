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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Long id;

	@Column(nullable = false, length = 45)
	private String email;

	@Column(nullable = false, length = 200)
	private String password;

	@Column(nullable = false, length = 21)
	private String nickname;

	@Column(nullable = false)
	@OneToMany(mappedBy = "user")
	private List<BackgroundUser> backgrounds = new ArrayList<>();

	@Column(nullable = false)
	private boolean isDeleted;

	@Builder
	public User(String email, String password, String nickname, boolean isDeleted) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.isDeleted = isDeleted;
	}

	public void insertBackgroundUser(BackgroundUser backgroundUser) {
		backgrounds.add(backgroundUser);
	}

}