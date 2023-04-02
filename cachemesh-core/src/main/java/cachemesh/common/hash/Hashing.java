package cachemesh.common.hash;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

// Based on github.com/redis/jedis: redis.clients.jedis.util.Hashing
public interface Hashing {

    default long hash(String key) {
        var keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return hash(keyBytes);
    }

    default long hash(byte[] key) {
        return hash(ByteBuffer.wrap(key));
    }

    long hash(ByteBuffer buf);

}
