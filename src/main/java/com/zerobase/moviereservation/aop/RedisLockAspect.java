package com.zerobase.moviereservation.aop;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_LOCKED;

import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

  private final RedisLockService redisLockService;

  @Around("@annotation(redisLock)")
  public Object roundLock(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
    String[] lockKeys = redisLock.keys();
    long timeout = redisLock.timeout();

    // 모든 lockKey에 대해 락 시도
    for (String lockKey : lockKeys) {
      if (!redisLockService.lock(lockKey, timeout)) {
        throw new CustomException(ALREADY_LOCKED);
      }
    }

    try {
      // 메서드 실행
      return joinPoint.proceed();
    } finally {
      // 모든 lockKey에 대해 락 해제
      for (String lockKey : lockKeys) {
        redisLockService.unlock(lockKey);
      }
    }
  }
}
