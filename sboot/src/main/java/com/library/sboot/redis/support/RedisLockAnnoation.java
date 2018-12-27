package com.library.sboot.redis.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisLockAnnoation {
    String keyPrefix() default "";

    /**
     * 要锁定的key中包含的属性
     */
    String[] keys() default {};

    /**
     * 是否阻塞锁
     * 1. true: 获取不到锁，阻塞一定时间
     * 2. false: 获取不到锁，立即返回
     */
    boolean isSpin() default false;

    /**
     * 超时时间
     */
    long expireTime() default 20000;

    /**
     * 等待时间
     */
    long waitTime() default 50;

    /**
     * 获取不到锁的等待时间
     */
    long retryTime() default 20;
}
