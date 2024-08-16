package com.ssafy.picple.domain.photouser.service;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;
import static com.ssafy.picple.domain.photouser.dto.response.PhotoUserResponseDto.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.photo.repository.PhotoRepository;
import com.ssafy.picple.domain.photouser.dto.request.PhotoUserRequestDto;
import com.ssafy.picple.domain.photouser.dto.response.PhotoUserResponseDto;
import com.ssafy.picple.domain.photouser.entity.PhotoUser;
import com.ssafy.picple.domain.photouser.repository.PhotoUserRepository;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoUserServiceImpl implements PhotoUserService {

	private final PhotoUserRepository photoUserRepository;
	private final PhotoRepository photoRepository;
	private final UserRepository userRepository;

	@Override
	public PhotoUserResponseDto getPhotoUserContent(PhotoUserRequestDto requestDto, Long userId) {
		PhotoUser photoUser = photoUserRepository.findByPhotoIdAndUserId(requestDto.getPhotoId(), userId);
		return photoUserResponseDto(photoUser);
	}

	@Override
	@Transactional
	public PhotoUserResponseDto createPhotoUser(PhotoUserRequestDto requestDto, Long userId) throws BaseException {
		// Photo와 User 조회, 없을 경우 예외 발생
		Photo photo = photoRepository.findById(requestDto.getPhotoId())
				.orElseThrow(() -> new BaseException(GET_PHOTO_EMPTY));
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BaseException(NOT_FOUND_USER));

		// TODO: 빌더 지양, 수정 필요
		// 빌더를 사용한 PhotoUser 엔티티 생성
		PhotoUser photoUser = PhotoUser.builder()
				.photo(photo)
				.user(user)
				.content(requestDto.getContent())
				.build();

		// 생성된 PhotoUser를 저장
		photoUserRepository.save(photoUser);

		// Response DTO로 변환하여 반환
		return photoUserResponseDto(photoUser);
	}

}
