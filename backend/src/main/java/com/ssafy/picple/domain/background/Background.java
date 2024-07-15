package com.ssafy.picple.domain.background;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "background")
@Getter
@Setter
public class Background {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long backgroundId;

    @Column(nullable = false, length = 45)
    private String backgroundTitle;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}