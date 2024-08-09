package com.ssafy.picple.domain.background.service;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.ssafy.picple.config.baseresponse.BaseException;

import reactor.core.publisher.Mono;

@Service
public class OpenAIService {

	private final FileUploadService fileUploadService;
	private final WebClient webClient;

	public OpenAIService(@Value("${openai.api-key}") String openAIApiKey, FileUploadService fileUploadService) {
		this.fileUploadService = fileUploadService;
		this.webClient = WebClient.builder()
				.exchangeStrategies(ExchangeStrategies.builder()
						.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
						.build())
				.baseUrl("https://api.openai.com/v1")
				.defaultHeader("Authorization", "Bearer " + openAIApiKey)
				.build();
	}

	/**
	 * 프롬프트로 DALL-E에게 8비트로 된 배경 사진 생성을 요청,
	 * Base64와 PNG로 된 사진 이름을 반환하는 메서드
	 *
	 * @param prompt 8비트 배경 이미지 생성을 위해 입력 받은 프롬프트
	 * @return 생성된 이미지의 S3 URL을 포함하는 문자열을 반환
	 * @throws BaseException 예외 발생 시
	 */
	public String[] createAIBackground(String prompt) throws BaseException {
		try {
			Mono<String> result = requestImageGeneration(prompt);

			String base64Image = fileUploadService.parseAIBackgroundImage(result);
			String fileName = fileUploadService.generateFileName("ai", ".png"); // 포맷을 PNG로 가정

			return new String[] {base64Image, fileName};
		} catch (Exception e) {
			// 기타 모든 예외 처리
			throw new BaseException(AI_CLIENT_ERROR);
		}
	}

	/**
	 * 요청된 프롬프트를 통해 DALL-E 모델로 커스텀 배경 사진을 생성하고
	 * 생성된 사진의 Base64 인코딩 문자열을 반환하는 메서드
	 *
	 * @param prompt 배경 사진 생성을 위해 사용. 프롬프트가 빈 경우 예외 발생
	 * @return 생성된 사진의 Base64 인코딩 문자열을 포함하는 Mono 객체 반환
	 * @throws BaseException 예외 발생 시
	 */
	private Mono<String> requestImageGeneration(String prompt) throws BaseException {

		// I/O 관련 오류 처리
		if (prompt.isEmpty())
			throw new BaseException(REQUEST_ERROR);

		Mono<String> result = this.webClient.post()
				.uri("/images/generations")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("""
						{
						  "prompt": "%s. 이를 주제로 한 8비트 스타일 배경 사진을 그려줘!",
						  "model": "dall-e-3",
						  "n": 1,
						  "quality": "standard",
						  "response_format": "b64_json",
						  "size": "1024x1024",
						  "style": "vivid"
						}
						""".formatted(prompt))
				.retrieve()
				.onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
						response -> response.bodyToMono(String.class).map(Exception::new))
				.onStatus(HttpStatus.UNAUTHORIZED::equals,
						response -> response.bodyToMono(String.class).map(Exception::new))
				.bodyToMono(String.class);

		return result;
	}
}