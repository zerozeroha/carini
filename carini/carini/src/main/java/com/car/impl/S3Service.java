package com.car.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;

@Service
public class S3Service {

	private final S3Client s3Client;

	@Value("${s3.bucket.name}")
	private String bucketName;

	public S3Service(S3Client s3Client) {
		this.s3Client = s3Client;
	}

	public void uploadFile(MultipartFile file) throws IOException {
		String originalFilename = file.getOriginalFilename();
		String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
	    String newFilename = UUID.randomUUID().toString() + extension;

		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(newFilename).build();
		
		
		
		s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
		
		// S3 객체의 URL 생성 (Pre-signed URL)
		GetUrlRequest getUrlRequest = GetUrlRequest.builder().bucket(bucketName).key(newFilename).build();
		
		URL url = s3Client.utilities().getUrl(getUrlRequest);
		
		// URL 문자열로 변환
		String urlString = url.toString();
		
		// URL 디코딩 수행(한글파일 전환)
		String decodedUrl = URLDecoder.decode(urlString, "UTF-8");

	}

	public String downloadFile(String key) throws IOException {
		// S3 객체의 URL 생성 (Pre-signed URL)
		GetUrlRequest getUrlRequest = GetUrlRequest.builder().bucket(bucketName).key(key).build();
		URL url = s3Client.utilities().getUrl(getUrlRequest);
		String urlString = url.toString();
		// URL 디코딩 수행
		String decodedUrl;
		decodedUrl = URLDecoder.decode(urlString, "UTF-8");

		return decodedUrl;
	}

}
