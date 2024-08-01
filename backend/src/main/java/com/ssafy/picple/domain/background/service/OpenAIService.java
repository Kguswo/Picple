package com.ssafy.picple.domain.background.service;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.background.dto.response.AIBackgroundResponse;

import reactor.core.publisher.Mono;

@Service
public class OpenAIService {

	private final WebClient webClient;
	private final S3Service s3Service;

	public OpenAIService(@Value("${openai.api-key}") String openAIApiKey, S3Service s3Service) {
		this.s3Service = s3Service;
		this.webClient = WebClient.builder()
				.exchangeStrategies(ExchangeStrategies.builder()
						.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
						.build())
				.baseUrl("https://api.openai.com/v1")
				.defaultHeader("Authorization", "Bearer " + openAIApiKey)
				.build();
	}

	/**
	 * 프롬프트로 DALL-E에게 8비트로 된 배경 이미지를 요청 후,
	 * AWS S3에 저장한 URL을 반환하는 메서드
	 *
	 * @param prompt 8비트 배경 이미지 생성을 위해 입력 받은 프롬프트
	 * @throws BaseException 예외 발생 시
	 */
	public String generateAndUploadBackgroundImage(Long userId, String prompt) throws BaseException {
		Mono<String> result = this.webClient.post()
				.uri("/images/generations")
				.contentType(MediaType.APPLICATION_JSON)
				// TODO: 여러 줄 할 때 따옴표 3개로 하는 방식으로도 시도해보기
				.bodyValue("{\n" +
						"  \"model\": \"dall-e-3\",\n" +
						"  \"prompt\": \"" + prompt + ", 8비트 스타일로 배경 사진을 그려줘\",\n" +
						"  \"n\": 1,\n" +
						"  \"size\": \"1024x1024\",\n" +
						"  \"response_format\": \"b64_json\"\n" +
						"}")
				.retrieve()
				.onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
						response -> response.bodyToMono(String.class).map(Exception::new))
				.onStatus(HttpStatus.UNAUTHORIZED::equals,
						response -> response.bodyToMono(String.class).map(Exception::new))
				.bodyToMono(String.class);

		// block() 부분 문제 해결 필요
		String responseBody = result.block();
		try {
			// JSON 파싱 및 처리
			ObjectMapper mapper = new ObjectMapper();
			AIBackgroundResponse openAIResponse = mapper.readValue(responseBody, AIBackgroundResponse.class);

			String base64Image = openAIResponse.getData().get(0).getBase64Json();
			String fileName = "backgrounds/8bit/" + UUID.randomUUID() + ".png"; // 이미지 포맷을 PNG로 가정

			// Base64 인코딩된 이미지를 S3에 업로드
			s3Service.uploadBase64ImageToS3(base64Image, fileName);

			return base64Image;

		} catch (JsonProcessingException e) {
			// JSON 파싱 오류 처리
			throw new BaseException(AI_RESPONSE_JSON_PARSING_ERROR);

		} catch (IOException e) {
			// I/O 관련 오류 처리
			throw new BaseException(REQUEST_ERROR);

		} catch (Exception e) {
			// 기타 모든 예외 처리
			throw new BaseException(AI_BACKGROUND_GENERATION_ERROR);
		}
	}
}