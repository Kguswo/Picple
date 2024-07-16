package com.ssafy.picple.domain.User.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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