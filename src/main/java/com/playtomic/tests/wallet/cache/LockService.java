package com.playtomic.tests.wallet.cache;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * Locking services to handle locking requirements
 */
@Slf4j
@Service
public class LockService {
	RedissonClient redissonClient;

	public LockService() {
		// connects with default value as localhost:6379
		redissonClient = Redisson.create();
	}

	public void lock(String key) {
		redissonClient.getLock(key).lock();
	}

	public void unlock(String key) {
		redissonClient.getLock(key).forceUnlock();
	}

	public boolean isLock(String key) {
		return redissonClient.getLock(key).isLocked();
	}
}
