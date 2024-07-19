package com.ssafy.picple.domain.background.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "background")
@Getter
public class Background {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "background_id")
	private Long id;

    @Column(nullable = false, length = 45)
    private String backgroundTitle;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}