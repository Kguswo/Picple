package com.ssafy.picple.domain.background.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "background")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Background {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String backgroundTitle;

//    @Column(nullable = false)
//    private LocalDateTime createdAt;

    @Builder
    public Background(String backgroundTitle) {
        this.backgroundTitle = backgroundTitle;
    }

}