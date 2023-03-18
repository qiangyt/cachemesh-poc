package cachemeshpoc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cachemeshpoc.err.MeshInternalException;

// Based on github.com/redis/jedis: redis.clients.jedis.util.Hashing
public class MD5Hash implements Hashing {

	public static final MD5Hash DEFAULT = new MD5Hash();

	public static final ThreadLocal<MessageDigest> TLS = ThreadLocal.withInitial(() -> {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new MeshInternalException(e, "md5 algorithm not available");
		}
	});

	@Override
	public long hash(byte[] key) {
		var d = TLS.get();
		d.reset();
		d.update(key);
		byte[] bKey = d.digest();

		return ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16)
				| ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
	}

}
