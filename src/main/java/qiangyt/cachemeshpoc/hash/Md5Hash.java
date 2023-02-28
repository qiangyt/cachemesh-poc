package qiangyt.cachemeshpoc.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Based on github.com/redis/jedis: redis.clients.jedis.util.Hashing
public class Md5Hash implements Hashing {

	public static final Md5Hash DEFAULT = new Md5Hash();

	private static final ThreadLocal<MessageDigest> TLS = new ThreadLocal<>() {

		@Override
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException("md5 algorithm not available");
			}
		}

	};

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
