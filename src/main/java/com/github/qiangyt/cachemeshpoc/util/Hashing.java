package com.github.qiangyt.cachemeshpoc.util;

import java.nio.charset.StandardCharsets;

// Based on github.com/redis/jedis: redis.clients.jedis.util.Hashing
public interface Hashing {

  public static final Hashing DEFAULT = MurmurHash.DEFAULT;

  default long hash(String key) {
		if (key == null) {
      throw new IllegalArgumentException("null value cannot be sent to redis");
    }
    var keyBytes = key.getBytes(StandardCharsets.UTF_8);
    return hash(keyBytes);
	}

  long hash(byte[] key);

}
