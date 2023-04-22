package cachemesh.common.config.op;

import java.util.ArrayList;
import java.util.Map;

import cachemesh.common.config.TypeOp;
import cachemesh.common.misc.StringHelper;

public abstract class AbstractOp<T> implements TypeOp<T> {

    public Iterable<Class<?>> convertableClasses() {
        return null;
    }

    @Override @SuppressWarnings("unchecked")
	public T populate(String hint, Map<String, Object> parent, Object value) {
        if (value == null) {
            return null;
        }

        if (klass().isAssignableFrom(value.getClass())) {
            return (T)value;
        }

        if (isConvertable(value) == false) {
            throw invalidValueClassError(hint, value);
        }

        return convert(hint, parent, value);
    }

    public boolean isConvertable(Object value) {
        var classes = convertableClasses();
        if (classes == null) {
            return false;
        }

        var actual = value.getClass();
        for (var expected : classes) {
            if (expected.isAssignableFrom(actual)) {
                return true;
            }
        }

        return false;
    }

    public T convert(String hint, Map<String, Object> parent, Object value) {
        throw new UnsupportedOperationException("To be implemented");
    }

    public IllegalArgumentException invalidValueClassError(String hint, Object value) {
        var classes = new ArrayList<Class<?>>();
        classes.add(klass());

        var others = convertableClasses();
        if (others != null) {
            for (var other : others) {
                classes.add(other);
            }
        }

        var msg = String.format("%s: expect be %s, but got %s", hint, StringHelper.join("/", classes),
                value.getClass());
        return new IllegalArgumentException(msg);
    }

}
