package com.ssafy.picple.domain.boardlike.entity;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "boardlike")
@Getter
@NoArgsConstructor
public class BoardLike {

	@Id
	@Column(name = "boardlike_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "board_id")
	private Board board;

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Boolean isLiked;

	@Builder
	public BoardLike(Long id, User user, Board board, Boolean isLiked) {
		this.id = id;
		this.user = user;
		this.board = board;
		this.isLiked = isLiked;
	}
}