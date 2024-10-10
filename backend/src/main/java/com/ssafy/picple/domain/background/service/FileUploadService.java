package com.ssafy.picple.domain.background.service;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.domain.background.dto.response.openai.AIBackgroundResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FileUploadService {

	/**
	 * OpenAI API 응답을 파싱하는 메서드
	 *
	 * @param result OpenAI API의 응답 본문
	 * @return Base64로 인코딩된 이미지 데이터
	 * @throws BaseException 예외 발생 시
	 */
	public String parseAIBackgroundImage(Mono<String> result) throws BaseException {
		try {
			String responseBody = result.block();
			ObjectMapper mapper = new ObjectMapper();
			AIBackgroundResponse response = mapper.readValue(responseBody, AIBackgroundResponse.class);

			return response.getData().get(0).getB64_json();
		} catch (JsonProcessingException e) {
			// JSON 파싱 오류 처리
			throw new BaseException(JSON_PARSING_ERROR);
		}
	}

	/**
	 * 파일 이름 생성 메서드
	 *
	 * @param directory 파일을 저장할 디렉토리
	 * @return 생성된 파일 이름
	 */
	public String generateFileName(String directory, String extension) {
		return String.format("%s/%s%s", directory, UUID.randomUUID(), extension);
	}

}
