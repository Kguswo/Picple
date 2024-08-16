package com.ssafy.picple.domain.background.service;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.domain.background.dto.response.BackgroundResponseDto;
import com.ssafy.picple.domain.background.entity.Background;
import com.ssafy.picple.domain.background.repository.BackgroundRepository;
import com.ssafy.picple.domain.backgrounduser.entity.BackgroundUser;
import com.ssafy.picple.domain.backgrounduser.repository.BackgroundUserRepository;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.repository.UserRepository;
import com.ssafy.picple.util.S3Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BackgroundServiceImpl implements BackgroundService {

	private final OpenAIService openAIService;
	private final S3Service s3Service;
	private final BackgroundRepository backgroundRepository;
	private final UserRepository userRepository;
	private final BackgroundUserRepository backgroundUserRepository;
	private final FileUploadService fileUploadService;

	// 기본 배경 사진을 DB에서 조회하여 반환
	@Override
	public List<BackgroundResponseDto> getDefaultBackgrounds() throws BaseException {
		return getBackgroundsBy(() -> backgroundRepository.findByIsDefault(true));
	}

	// 특정 사용자가 추가한 배경 사진을 DB에서 조회하여 반환
	@Override
	public List<BackgroundResponseDto> getUserBackgrounds(Long userId) throws BaseException {
		Long id = userRepository.findById(userId).get().getId();
		if (id == null) {
			throw new BaseException(GET_USER_EMPTY);
		}

		return getBackgroundsBy(() -> backgroundRepository.findByUserId(userId));
	}

	/*
	 배경 사진 리스트를 조회하고, BackgroundResponseDto로 변환
	 getDefaultBackgrounds와 getUserBackgrounds에서 쓰임
	 */
	private List<BackgroundResponseDto> getBackgroundsBy(Supplier<List<Background>> backgroundSupplier) throws
			BaseException {
		try {
			List<Background> backgrounds = backgroundSupplier.get();
			return backgrounds.stream()
					.map(BackgroundResponseDto::backgroundResponseDto)
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// AI를 통해 배경 사진을 생성하여 S3와 DB에 저장
	@Override
	@Transactional
	public String createAIBackground(Long userId, String prompt) throws BaseException {
		try {
			// 프롬프트를 통해 AI API를 사용하여 사진 생성
			String[] result = openAIService.createAIBackground(prompt);
			String url = s3Service.uploadBase64ImageToS3(result[0], result[1]);
			saveBackgroundToDB(userId, result[0], result[1]);
			return url;
		} catch (Exception e) {
			// 예외 처리
			throw new BaseException(BACKGROUND_UPLOAD_ERROR);
		}
	}

	// 로컬에서 업로드된 파일을 S3와 DB에 저장
	@Override
	@Transactional
	public void createLocalBackground(Long userId, MultipartFile file) throws BaseException {
		try {
			String fileName = fileUploadService.generateFileName("local", ".png");
			String backgroundUrl = s3Service.uploadMultipartFileImageToS3(file, fileName);
			saveBackgroundToDB(userId, backgroundUrl, fileName);

		} catch (Exception e) {
			throw new BaseException(BACKGROUND_UPLOAD_ERROR);
		}
	}

	// 배경 사진의 정보, 사용자와 배경 사진의 관계를 DB에 저장
	private void saveBackgroundToDB(Long userId, String backgroundUrl, String fileName) throws BaseException {
		try {
			// 데이터베이스에 저장
			Background background = Background.builder()
					.backgroundTitle(fileName)
					.backgroundUrl(backgroundUrl)
					.build();
			backgroundRepository.save(background);

			// Background와 User의 관계 저장
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new BaseException(NOT_FOUND_USER));

			BackgroundUser backgroundUser = BackgroundUser.builder()
					.background(background)
					.user(user)
					.build();
			backgroundUserRepository.save(backgroundUser);
	    } catch (Exception e) {
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// 배경 사진을 DB에서 삭제
	@Override
	@Transactional
	public void deleteBackground(Long backgroundId, Long userId) throws BaseException {
		try {
			backgroundRepository.deleteBackground(userId, backgroundId);
		} catch (Exception e) {
			throw new BaseException(DELETE_BACKGROUND_ERROR);
		}
	}

}
