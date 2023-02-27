package com.github.qiangyt.cachemeshpoc.util;

import java.nio.charset.StandardCharsets;

// Based on github.com/redis/jedis: redis.clients.jedis.util.Hashing
public interface Hashing {

  default long hash(String key) {
		if (key == null) {
      throw new IllegalArgumentException("hash key cannot be null");
    }
    var keyBytes = key.getBytes(StandardCharsets.UTF_8);
    return hash(keyBytes);
	}

  long hash(byte[] key);

}
