package com.ssafy.picple.domain.background.entity;

import java.time.LocalDateTime;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "background")
@Getter
@NoArgsConstructor
public class Background {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "background_id")
	private Long id;

	@Column(nullable = false)
	@OneToMany(mappedBy = "background")
	private List<BackgroundUser> users = new ArrayList<>();

	@Column(nullable = false, length = 45)
	private String backgroundTitle;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Boolean isDeleted;

	@Builder
	public Background(String backgroundTitle) {
		this.backgroundTitle = backgroundTitle;
		this.createdAt = LocalDateTime.now();
		this.isDeleted = false;
	}

	public void insertBackgroundUser(BackgroundUser backgroundUser) {
		users.add(backgroundUser);
	}

	public void modifyTitle(String newTitle) {
		this.backgroundTitle = newTitle;
	}

	public void deleteBackground(Background background) {
		background.isDeleted = true;
	}
}