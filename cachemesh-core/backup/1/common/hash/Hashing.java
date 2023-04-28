package cachemesh.common.hash;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

// Based on github.com/redis/jedis: redis.clients.jedis.util.Hashing
public interface Hashing {

    default long hash(@Nonnull String key) {
        var keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return hash(keyBytes);
    }

    default long hash(@Nonnull byte[] key) {
        return hash(ByteBuffer.wrap(key));
    }

    long hash(@Nonnull ByteBuffer buf);

}
