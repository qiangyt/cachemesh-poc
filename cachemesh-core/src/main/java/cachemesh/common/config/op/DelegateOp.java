package cachemesh.common.config.op;

import java.util.Map;

import cachemesh.common.config.TypeOp;

public abstract class DelegateOp<T> implements TypeOp<T> {

	public abstract TypeOp<T> target();

	@Override
	public Class<?> klass() {
		return target().klass();
	}

	@Override
	public T populate(String hint, Map<String, Object> parent, Object value) {
		return target().populate(hint, parent, value);
	}

}
