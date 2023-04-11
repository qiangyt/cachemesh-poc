package cachemesh.common.config.op;

import cachemesh.common.config.TypeOp;

public class StaticDelegateOp<T> extends DelegateOp<T> {

	private final TypeOp<T> target;

	public StaticDelegateOp(TypeOp<T> target) {
		this.target = target;
	}

	public TypeOp<T> target() {
		return this.target;
	}

}
