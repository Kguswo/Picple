package com.ssafy.picple.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

	// Base64, URL 중 우리가 쓰는 것으로 선택

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	/**
	 * Base64로 인코딩된 이미지를 S3에 저장하는 메서드
	 *
	 * @param base64Image Base64로 인코딩된 이미지 데이터
	 * @param fileName 파일 이름
	 * @return 업로드된 이미지의 URL
	 */
	public String uploadBase64ImageToS3(String base64Image, String fileName) {
		byte[] imageBytes = Base64.getDecoder().decode(base64Image);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(imageBytes.length);
		metadata.setContentType("image/png"); // 8비트 이미지는 png일 가능성이 높음

		amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
		return amazonS3.getUrl(bucketName, fileName).toString();
	}

	/**
	 * 이미지 URL을 S3에 저장하는 메서드
	 *
	 * @param imageUrl 이미지 URL
	 * @param fileName 저장할 파일 이름
	 * @return 업로드된 이미지의 URL
	 */
	public String uploadImageFromUrlToS3(String imageUrl, String fileName) throws IOException {
		URL url = new URL(imageUrl);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		InputStream inputStream = connection.getInputStream();

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(connection.getContentLength());
		metadata.setContentType(connection.getContentType());

		amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
		return amazonS3.getUrl(bucketName, fileName).toString();
	}
}

