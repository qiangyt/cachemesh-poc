package com.github.qiangyt.cachemeshpoc.caffeine;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class CaffeineLocalCache {

	private static final Logger LOG = LoggerFactory.getLogger(CaffeineLocalCache.class);

	private Cache<String, Object> cache;

	public CaffeineLocalCache() {
		this.cache = Caffeine.newBuilder()
				.maximumSize(10_000)
				.expireAfterWrite(Duration.ofMinutes(5))
				.refreshAfterWrite(Duration.ofMinutes(1))
				.build();
	}

}
