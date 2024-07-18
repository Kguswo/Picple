package com.ssafy.picple.domain.photo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "photo")
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

    @Builder
    public Photo(String photoUrl, boolean isShared, boolean isDeleted) {
        this.photoUrl = photoUrl;
        this.isShared = isShared;
        this.isDeleted = isDeleted;
    }

}
