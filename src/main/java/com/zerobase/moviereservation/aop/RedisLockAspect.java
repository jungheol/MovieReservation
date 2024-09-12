package com.zerobase.moviereservation.aop;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_LOCKED;

import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.service.RedisLockService;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

  private final RedisLockService redisLockService;
  private final SpelExpressionParser parser = new SpelExpressionParser();

  @Around("@annotation(redisLock)")
  public Object roundLock(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    // 메서드의 파라미터 값 가져오기
    Object[] args = joinPoint.getArgs();
    String[] parameterNames = signature.getParameterNames();
    StandardEvaluationContext context = new StandardEvaluationContext();

    // 파라미터 값을 SpEL에서 사용 가능하도록 추가
    for (int i = 0; i < parameterNames.length; i++) {
      context.setVariable(parameterNames[i], args[i]);
    }

    // SpEL 표현식을 평가하여 락 키 생성
    String[] keyExpressions = redisLock.keys();
    long timeout = redisLock.timeout();

    Set<String> lockKeys = new HashSet<>();
    for (String expression : keyExpressions) {
      org.springframework.expression.Expression expr = parser.parseExpression(expression);
      String[] evaluatedKeys = expr.getValue(context, String[].class);
      lockKeys.addAll(Arrays.asList(evaluatedKeys));
    }

    try {
      for (String key : lockKeys) {
        if (!redisLockService.lock(key, timeout)) {
          throw new CustomException(ALREADY_LOCKED);
        }
      }

      // 메서드 실행
      return joinPoint.proceed();
    } finally {
      // 모든 lockKey에 대해 락 해제
      for (String key : lockKeys) {
        redisLockService.unlock(key);
      }
    }
  }
}
