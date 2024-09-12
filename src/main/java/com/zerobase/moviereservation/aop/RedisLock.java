package com.zerobase.moviereservation.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
  String[] keys(); // 락을 걸 때 사용할 key를 지정
  long timeout() default 10; // 기본 timeout 값
}
