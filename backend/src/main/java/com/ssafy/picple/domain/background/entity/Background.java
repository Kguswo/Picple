package com.ssafy.picple.domain.background.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ssafy.picple.domain.backgrounduser.entity.BackgroundUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "background")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Background {

	@Id
	@Column(name = "background_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@OneToMany(mappedBy = "background")
	private List<BackgroundUser> users = new ArrayList<>();

	@Column(nullable = false, length = 45)
	private String backgroundTitle;

//    @Column(nullable = false)
//    private LocalDateTime createdAt;

    @Builder
    public Background(String backgroundTitle) {
        this.backgroundTitle = backgroundTitle;
    }

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Boolean isDeleted;

	public void insertBackgroundUser(BackgroundUser backgroundUser) {
		users.add(backgroundUser);
	}

	public void modifyTitle(String newTitle) {
		this.backgroundTitle = newTitle;
	}

	public void deleteBackground(Background background) {
		background.isDeleted = true;
	}
}