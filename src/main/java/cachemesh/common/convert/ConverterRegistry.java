package cachemesh.common.convert;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.err.InternalException;

public class ConverterRegistry {

	public static final ConverterRegistry DEFAULT = new ConverterRegistry();

	static class Key {
		final Class<?> source;
		final Class<?> target;
		final int hash;

		public Key(Class<?> source, Class<?> target) {
			this.source = source;
			this.target = target;
			this.hash = (source.getName() + target.getName()).hashCode();
		}

		@Override
		public int hashCode() {
			return this.hash;
		}

		@Override
		public boolean equals(Object obj) {
			var that = (Key)obj;
			return that.source == this.source && that.target == this.target;
		}
	}

	private Map<Key, Converter> all = new HashMap<>();

	public <S,T> void register(Class<S> srcType, Class<T> targetType, Converter<S,T> converter) {
		var key = new Key(srcType, targetType);

		this.all.compute(key, (k, prev) -> {
			if (prev != null) {
				throw new InternalException("duplicated converter for %s -> %s", srcType, targetType);
			}
			return converter;
		});
	}

	public <S,T> Converter<S,T> get(Class<S> srcType, Class<T> targetType) {
		return this.all.get(new Key(srcType, targetType));
	}

}
