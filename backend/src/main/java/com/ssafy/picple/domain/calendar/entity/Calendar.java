package com.ssafy.picple.domain.calendar.entity;

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
@Table(name = "calendar")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "calendar_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "photo_id", nullable = false)
	private Photo photo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public Calendar(Photo photo, User user) {
		this.photo = photo;
		this.user = user;
	}
}
