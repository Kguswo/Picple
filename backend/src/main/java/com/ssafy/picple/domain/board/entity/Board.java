package com.ssafy.picple.domain.board.entity;

import com.ssafy.picple.config.BaseTimeEntity;
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
@Table(name = "board")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

	@Id
	@Column(name = "board_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "photo_id")
	private Photo photo;

	@Column(nullable = false)
	private int hit;

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isDeleted;

	@Builder
	public Board(User user, Photo photo, int hit, boolean isDeleted) {
		this.user = user;
		this.photo = photo;
		this.hit = hit;
		this.isDeleted = isDeleted;
	}

}