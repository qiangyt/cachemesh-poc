/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */

package cachemesh.common.hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

/**
 * Copy of github.com/redis/jedis: redis.clients.jedis.util.MurmurHash
 *
 * This is a very fast, non-cryptographic hash suitable for general hash-based lookup. See
 * http://murmurhash.googlepages.com/ for more details. <br>
 * <p>
 * The C version of MurmurHash 2.0 found at that site was ported to Java by Andrzej Bialecki (ab at getopt org).
 * </p>
 */
public class MurmurHash implements Hashing {

    public static final MurmurHash DEFAULT = new MurmurHash();

    /**
     * Hashes bytes in an array.
     *
     * @param data
     *            The bytes to hash.
     * @param seed
     *            The seed for the hash.
     *
     * @return The 32 bit hash of the bytes in question.
     */
    public static int hash(byte[] data, int seed) {
        return hash(ByteBuffer.wrap(data), seed);
    }

    /**
     * Hashes bytes in part of an array.
     *
     * @param data
     *            The data to hash.
     * @param offset
     *            Where to start munging.
     * @param length
     *            How many bytes to process.
     * @param seed
     *            The seed to start with.
     *
     * @return The 32-bit hash of the data in question.
     */
    public static int hash(@Nonnull byte[] data, int offset, int length, int seed) {
        checkNotNull(data);

        return hash(ByteBuffer.wrap(data, offset, length), seed);
    }

    /**
     * Hashes the bytes in a buffer from the current position to the limit.
     *
     * @param buf
     *            The bytes to hash.
     * @param seed
     *            The seed for the hash.
     *
     * @return The 32 bit murmur hash of the bytes in the buffer.
     */
    public static int hash(@Nonnull ByteBuffer buf, int seed) {
        checkNotNull(buf);

        // save byte order for later restoration
        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        int m = 0x5bd1e995;
        int r = 24;

        int h = seed ^ buf.remaining();

        int k;
        while (buf.remaining() >= 4) {
            k = buf.getInt();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h *= m;
            h ^= k;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            // for big-endian version, use this first:
            // finish.position(4-buf.remaining());
            finish.put(buf).rewind();
            h ^= finish.getInt();
            h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        buf.order(byteOrder);
        return h;
    }

    public static long hash64A(@Nonnull byte[] data, int seed) {
        checkNotNull(data);

        return hash64A(ByteBuffer.wrap(data), seed);
    }

    public static long hash64A(@Nonnull byte[] data, int offset, int length, int seed) {
        checkNotNull(data);

        return hash64A(ByteBuffer.wrap(data, offset, length), seed);
    }

    public static long hash64A(@Nonnull ByteBuffer buf, int seed) {
        checkNotNull(buf);

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            // for big-endian version, do this first:
            // finish.position(8-buf.remaining());
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }

    @Override
    public long hash(@Nonnull byte[] key) {
        checkNotNull(key);
        return hash64A(key, 0x1234ABCD);
    }

    @Override
    public long hash(@Nonnull ByteBuffer buf) {
        checkNotNull(buf);
        return hash(buf, 0x1234ABCD);
    }

}
