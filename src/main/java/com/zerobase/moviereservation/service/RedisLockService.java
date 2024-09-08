package com.zerobase.moviereservation.service;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisLockService {

  private final StringRedisTemplate redisTemplate;

  public RedisLockService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public boolean lock(String key, String value, long timeoutInSeconds) {
    // SETNX: key가 존재하지 않으면 값을 설정하고 true를 반환, 이미 있으면 false 반환
    Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, timeoutInSeconds, TimeUnit.SECONDS);
    return Boolean.TRUE.equals(success);
  }

  public void unlock(String key, String value) {
    // Redis에서 값을 조회하여 일치할 경우에만 락을 해제
    String currentValue = redisTemplate.opsForValue().get(key);
    if (value.equals(currentValue)) {
      redisTemplate.delete(key);
    }
  }

}
