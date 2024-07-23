package com.ssafy.picple.domain.background.service;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.background.entity.Background;
import com.ssafy.picple.domain.background.repository.BackgroundRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BackgroundServiceImpl implements BackgroundService {

	final BackgroundRepository backgroundRepository;

	@Override
	public List<Background> getDefaultBackgrounds() {
		return backgroundRepository.findAll();
	}

	@Override
	public List<Background> getUserBackgrounds(Long userId) {
		List<Background> result = backgroundRepository.findByUserId(userId);
		return result;
	}

	@Override
	public Background insertAIBackground(Long userId, String prompt) {

		// TODO: AI API를 사용하여 prompt에 적힌 이미지 생성
		Background background = new Background();

		return backgroundRepository.save(background);
	}

	@Override
	public Background insertLocalBackground(Long userId, MultipartFile file) {

		// TODO: 파일 업로드 로직 구현
		Background background = new Background(file.getOriginalFilename());

		return backgroundRepository.save(background);
	}

	@Override
	public Background deleteBackground(Long backgroundId, Long userId) throws BaseException {

		Background background = backgroundRepository.findById(backgroundId)
				.orElseThrow(() -> new BaseException(INVALID_BACKGROUND_ID));

		background.deleteBackground(background);
		return background;
	}

}
