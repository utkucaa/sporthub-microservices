package com.sporthub.rezervation_service.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedisDistributedLock {

    private final RedissonClient redissonClient;
    private final long lockTtlSeconds;

    public RedisDistributedLock(RedissonClient redissonClient,
                                @Value("${sporthub.reservation.lock.ttl-seconds:30}") long lockTtlSeconds) {
        this.redissonClient = redissonClient;
        this.lockTtlSeconds = lockTtlSeconds;
    }

    public <T> T executeWithLock(String lockKey, Supplier<T> action) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(2, lockTtlSeconds, TimeUnit.SECONDS);
            if (!acquired) {
                throw new IllegalStateException("Could not acquire lock for key: " + lockKey);
            }
            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while acquiring lock for key: " + lockKey);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}


