package com.ssafy.picple.domain.photo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "photo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2083)
    private String photoUrl;

    @Column(nullable = false)
    private boolean isShared = false;

    @Column(nullable = false)
    private boolean isDeleted;

}
