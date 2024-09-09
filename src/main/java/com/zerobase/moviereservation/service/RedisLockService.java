package com.zerobase.moviereservation.service;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class RedisLockService {

  private final StringRedisTemplate redisTemplate;

  public boolean lock(String key, long timeoutInSeconds) {
    // SETNX: key가 존재하지 않으면 값을 설정하고 true를 반환, 이미 있으면 false 반환
    log.info("START LOCK || " + key);

    return Boolean.TRUE.equals(
        redisTemplate
            .opsForValue()
            .setIfAbsent(key, "locked", timeoutInSeconds, TimeUnit.SECONDS));
  }

  public void unlock(String key) {
    // Redis에서 값을 조회하여 일치할 경우에만 락을 해제
    log.info("END LOCK || " + key);

    if (key.equals(redisTemplate.opsForValue().get(key))) {
      redisTemplate.delete(key);
    }
  }
}
