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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BackgroundServiceImpl implements BackgroundService {

	private final BackgroundRepository backgroundRepository;

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

	// 수정 필요
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

	// 수정 필요
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
			backgroundRepository.deleteBackground(userId, backgroundId);
		} catch (Exception e) {
			throw new BaseException(DELETE_BACKGROUND_ERROR);
		}
	}

}
