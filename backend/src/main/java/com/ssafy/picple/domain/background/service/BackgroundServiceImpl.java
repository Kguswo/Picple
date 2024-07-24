package com.ssafy.picple.domain.background.service;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.background.dto.response.BackgroundResponseDto;
import com.ssafy.picple.domain.background.entity.Background;
import com.ssafy.picple.domain.background.repository.BackgroundRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BackgroundServiceImpl implements BackgroundService {

	final BackgroundRepository backgroundRepository;

	@Override
	public List<BackgroundResponseDto> getDefaultBackgrounds() throws BaseException {
		try {
			return backgroundRepository.findByIsDefault(true).stream()
					.map(BackgroundResponseDto::backgroundResponseDto)
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new BaseException(DATABASE_ERROR);
		}
	}

	@Override
	public List<BackgroundResponseDto> getUserBackgrounds(Long userId) {
		List<BackgroundResponseDto> backgrounds = backgroundRepository.findByUserId(userId).stream()
				.map(BackgroundResponseDto::backgroundResponseDto)
				.collect(Collectors.toList());
		return backgrounds;
	}

	@Override
	@Transactional
	public void insertAIBackground(Long userId, String prompt) throws BaseException {
		try {
			// TODO: AI API를 사용하여 prompt에 적힌 이미지 생성
			Background background = new Background("temp");
			backgroundRepository.save(background);
		} catch (Exception e) {
			throw new BaseException(AI_BACKGROUND_GENERATION_ERROR);
		}
	}

	@Override
	@Transactional
	public void insertLocalBackground(Long userId, MultipartFile file) throws BaseException {
		try {
			// TODO: 파일 업로드 로직 구현
			Background background = new Background("temp");
			backgroundRepository.save(background);
		} catch (Exception e) {
			throw new BaseException(LOCAL_BACKGROUND_UPLOAD_ERROR);
		}
	}

	@Override
	@Transactional
	public void deleteBackground(Long backgroundId, Long userId) throws BaseException {
		try {
			Background background = backgroundRepository.findById(backgroundId)
					.orElseThrow(() -> new BaseException(INVALID_BACKGROUND_ID));
			if (!background.hasUserId(userId)) {
				throw new BaseException(INVALID_USER_JWT);
			}
			background.deleteBackground(background);
		} catch (Exception e) {
			throw new BaseException(DELETE_BACKGROUND_ERROR);
		}
	}

}
