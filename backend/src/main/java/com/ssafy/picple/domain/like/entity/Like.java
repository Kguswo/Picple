package com.ssafy.picple.domain.like.entity;

import com.ssafy.picple.domain.board.entity.Board;
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
@Table(name = "like")
@Getter
public class Like {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long likeId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "board_id")
	private Board board;

	@Column(nullable = false)
	private Boolean isLiked;

}