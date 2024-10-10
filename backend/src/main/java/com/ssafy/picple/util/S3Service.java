package com.ssafy.picple.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	 * @param fileName    파일 이름
	 */
	public String uploadBase64ImageToS3(String base64Image, String fileName) {
		byte[] imageBytes = Base64.getDecoder().decode(base64Image);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(imageBytes.length);
		metadata.setContentType("image/png"); // 이미지는 png일 가능성이 높음

		amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
		return amazonS3.getUrl(bucketName, fileName).toString();
	}

	/**
	 * 사용자의 로컬 사진(MultipartFile)을 S3에 저장하는 메서드
	 *
	 * @param file MultipartFile, 로컬 파일
	 * @param fileName 파일 이름
	 */
	public String uploadMultipartFileImageToS3(MultipartFile file, String fileName) throws IOException {
		InputStream inputStream = file.getInputStream();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());
		metadata.setContentType("image/png"); // 이미지는 png일 가능성이 높음

		amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
		return amazonS3.getUrl(bucketName, fileName).toString();
	}

	/**
	 * S3에서 파일의 URL을 반환하는 메서드
	 *
	 * @param fileName S3에 저장된 파일의 이름
	 * @return 파일의 URL
	 */
	public String getFileUrl(String fileName) {
		return amazonS3.getUrl(bucketName, fileName).toString();
	}
}

