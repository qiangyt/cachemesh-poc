package cachemesh.common.config;

import cachemesh.common.config.op.BooleanOp;
import cachemesh.common.config.op.ClassOp;
import cachemesh.common.config.op.DurationOp;
import cachemesh.common.config.op.EnumOp;
import cachemesh.common.config.op.IntegerOp;
import cachemesh.common.config.op.ReflectOp;
import cachemesh.common.config.op.SimpleUrlOp;
import cachemesh.common.config.op.StringOp;
import cachemesh.common.misc.ClassCache;
import cachemesh.common.misc.SimpleRegistry;

public class TypeOpRegistry extends SimpleRegistry<Class<?>, TypeOp<?>>{

	public static final TypeOpRegistry DEFAULT = buildDefault();

	public static TypeOpRegistry buildDefault() {
		var r = new TypeOpRegistry();

		r.register(new BooleanOp());
		r.register(new ClassOp(ClassCache.DEFAULT));
		r.register(new DurationOp());
		r.register(new IntegerOp());
		r.register(new SimpleUrlOp());
		r.register(new StringOp());

		return r;
	}

    public <T> void register(TypeOp<T> op) {
		register(op.klass(), op);
	}

	public <T extends Enum<T>> void registerEnum(Class<T> enumClass) {
		var op = new EnumOp<T>(enumClass);
		register(op);
	}

	public <T extends Bean> void registerBean(Class<T> enumClass) {
		var op = new ReflectOp<T>(enumClass);
		register(op);
	}

	@Override
	protected String supplyKey(Class<?> config) {
		return config.getCanonicalName();
	}

}
