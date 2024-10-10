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

/**
 * 사용자와 사진 간의 관계를 나타내는 엔티티 클래스
 * 특정 사용자가 사진에 대해 작성한 설명을 저장
 * 지연 로딩 방식으로 설정, 실제로 사용될 때 DB에서 조회됨
 */

@Entity
@Getter
@Table(name = "photo_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoUser {

	@Id
	@Column(name = "photo_user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 고유 식별자

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "photo_id")
	private Photo photo; // 연결된 사진을 참조

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user; // 연결된 시용자를 참조

	@Column(length = 50)
	private String content; // 사진에 작성된 설명

	/**
	 * PhotoUser 객체를 생성하는 빌더
	 * 사진, 사용자, 내용 정보를 사용하여 생성
	 */
	@Builder
	public PhotoUser(Photo photo, User user, String content) {
		this.photo = photo;
		this.user = user;
		this.content = content;
	}

	/**
	 * 사진에 대한 설명을 수정
	 * 주어진 내용으로 content 필드 업데이트
	 */
	public void setContent(String content) {
		this.content = content;
	}
}