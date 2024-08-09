package com.ssafy.picple.util;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisUtil {

	// Redis와의 상호작용을 위함
	private final StringRedisTemplate template;

	// 주어진 키에 해당하는 값을 Redis에서 조회
	public String getData(String key) {
		ValueOperations<String, String> valueOperations = template.opsForValue();
		return valueOperations.get(key);
	}

	// 주어진 키에 해당하는 데이터가 Redis에 존재하는지 확인
	public boolean existData(String key) {
		return Boolean.TRUE.equals(template.hasKey(key));
	}

	// 주어진 키와 값으로 데이터를 Redis에 저장하며, 특정 기간 후 만료되도록 설정
	public void setDataExpire(String key, String value, long duration) {
		ValueOperations<String, String> valueOperations = template.opsForValue();
		Duration expireDuration = Duration.ofSeconds(duration); // 만료 기간을 초 단위로 설정
		valueOperations.set(key, value, expireDuration); // Redis에 데이터 저장 및 만료 시간 설정
	}

	// 주어진 키에 해당하는 데이터를 Redis에서 삭제
	public void deleteData(String key) {
		template.delete(key);
	}
}
