package com.ssafy.picple.domain.background.service;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseResponse.BaseException;
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
	private final LocalFileService localFileService;

	@Override
	public List<BackgroundResponseDto> getDefaultBackgrounds() throws BaseException {
		return getBackgroundsBy(() -> backgroundRepository.findByIsDefault(true));
	}

	@Override
	public List<BackgroundResponseDto> getUserBackgrounds(Long userId) throws BaseException {
		return getBackgroundsBy(() -> backgroundRepository.findByUserId(userId));
	}

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

	// 테스트 필요
	@Override
	@Transactional
	public void createAIBackground(Long userId, String prompt) throws BaseException {
		try {
			// 프롬프트를 통해 AI API를 사용하여 사진 생성
			String[] result = openAIService.createBackground(prompt);
			String base64Image = result[0];
			String fileName = result[1];
			String[] result = openAIService.createAIBackground(prompt);
			saveBackground(userId, result);

		} catch (Exception e) {
			// 예외 처리
			throw new BaseException(AI_BACKGROUND_GENERATION_ERROR);
		}
	}

	private void saveBackground(Long userId, String[] result) throws BaseException {
		String base64Image = result[0];
		String fileName = result[1];

		// S3에 업로드
		s3Service.uploadBase64ImageToS3(base64Image, fileName);

		// 데이터베이스에 저장
		Background background = Background.builder()
				.backgroundTitle(fileName)
				.backgroundUrl(base64Image)
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
	}

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
