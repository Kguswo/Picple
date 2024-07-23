package com.ssafy.picple.domain.background.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.ssafy.picple.domain.background.entity.Background;
import com.ssafy.picple.domain.backgrounduser.entity.BackgroundUser;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DefaultBackgroundResponse extends BackgroundResponseDto {
	private List<Long> userIds;

	public static DefaultBackgroundResponse from(Background background) {
		return DefaultBackgroundResponse.builder()
				.id(background.getId())
				.backgroundTitle(background.getBackgroundTitle())
				.imageUrl(background.getBackgroundUrl())
				.isDefault(background.getIsDefault())
				.isDeleted(background.getIsDeleted())
				.userIds(background.getUsers().stream()
						.map(BackgroundUser::getId)
						.collect(Collectors.toList()))
				.build();
	}
}
