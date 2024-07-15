package com.ssafy.picple.domain.photoUser.entity;

import com.ssafy.picple.domain.User.entity.User;
import com.ssafy.picple.domain.photo.entity.Photo;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "photo_user")
@Getter
public class PhotoUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 50)
    private String content;

}