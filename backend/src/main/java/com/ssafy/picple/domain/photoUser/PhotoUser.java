package com.ssafy.picple.domain.photoUser;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "photo_user")
@Getter
@Setter
public class PhotoUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoUserId;

    @ManyToOne
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 50)
    private String content;

}