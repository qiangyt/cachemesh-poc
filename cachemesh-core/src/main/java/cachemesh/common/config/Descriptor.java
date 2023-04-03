package cachemesh.common.config;

import java.util.ArrayList;
import java.util.Collection;

import cachemesh.common.util.StringHelper;

public abstract class Descriptor<T> {

    public abstract Class<?> propertyClass();

    public Collection<Class<?>> convertableClasses() {
        return null;
    }

    public abstract T defaultValue();

    public abstract T createEmptyValue();

    @SuppressWarnings("unchecked")
    public T supply(String hint, Object value) {
        return (T) value;
    }

    public T copy(String hint, T value) {
        return value;
    }

    public T convert(String hint, Object value) {
        if (value == null) {
            return null;
        }
        if (propertyClass().isAssignableFrom(value.getClass())) {
            return supply(hint, value);
        }

        if (isConvertable(value) == false) {
            throw invalidValueClassError(hint, value);
        }

        return doConvert(hint, value);
    }

    public boolean isConvertable(Object value) {
        var classes = convertableClasses();
        if (classes == null || classes.isEmpty()) {
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

    public T doConvert(String hint, Object value) {
        throw new UnsupportedOperationException("To be implemented");
    }

    public IllegalArgumentException invalidValueClassError(String hint, Object value) {
        var classes = new ArrayList<Class<?>>();
        classes.add(propertyClass());

        var others = convertableClasses();
        if (others != null && others.isEmpty() == false) {
            classes.addAll(others);
        }

        var msg = String.format("%s: expect be %s, but got %s", hint, StringHelper.join("/", classes),
                value.getClass());
        return new IllegalArgumentException(msg);
    }

}
