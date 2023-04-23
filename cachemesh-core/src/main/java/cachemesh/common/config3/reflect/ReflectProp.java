/*
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cachemesh.common.config3.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cachemesh.common.config3.AbstractProp;
import cachemesh.common.config3.Prop;
import cachemesh.common.config3.Type;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.config3.annotations.Default;
import cachemesh.common.config3.annotations.DefaultBoolean;
import cachemesh.common.config3.annotations.DefaultClass;
import cachemesh.common.config3.annotations.DefaultDuration;
import cachemesh.common.config3.annotations.DefaultEnum;
import cachemesh.common.config3.annotations.DefaultInt;
import cachemesh.common.config3.annotations.Property;
import cachemesh.common.config3.annotations.PropertyElement;
import cachemesh.common.config3.types.ArrayType;
import cachemesh.common.config3.types.EnumType;
import cachemesh.common.config3.types.ListType;
import cachemesh.common.config3.types.MapType;
import cachemesh.common.misc.DurationHelper;
import cachemesh.common.misc.Reflect;
import lombok.Getter;

@Getter
public class ReflectProp<B, T> extends AbstractProp<B, T> {

    private final Method setter;

    private final Method getter;

    protected ReflectProp(String name, Type<T> fieldType, T devault, Method getter, Method setter) {
        super(name, fieldType, devault);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void set(B bean, T value) {
        Reflect.set(getSetter(), bean, value);
    }

    @Override
    public T get(B bean) {
        return Reflect.get(getGetter(), bean);
    }

    @SuppressWarnings("unchecked")
    public static <B> Map<String, Prop<B, ?>> of(TypeRegistry typeRegistry, Class<B> klass) {
        var r = new HashMap<String, Prop<B, ?>>();

        for (var f : klass.getDeclaredFields()) {
            var p = (Prop<B,?>)ReflectProp.of(typeRegistry, f);
            r.put(p.getName(), p);
        }

        return r;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ReflectProp<?, ?> of(TypeRegistry typeRegistry, Field field) {
        String propName = null;
        String setterName = null;
        String getterName = null;

        var pa = field.getAnnotation(Property.class);
        if (pa != null) {
            if (pa.ignore()) {
                return null;
            }
            propName = pa.value();
            setterName = pa.setter();
            getterName = pa.getter();
        }

        Class fClass = field.getType();
        var beanClass = field.getDeclaringClass();

        propName = Reflect.propertyName(field, propName);

        var setter = Reflect.setter(beanClass, propName, fClass, setterName);
        var getter = Reflect.getter(beanClass, propName, fClass, getterName);

        Type fType;
        if (fClass.isArray()) {
            fType = ArrayType.of(typeRegistry, (Class<Object[]>)fClass);
        } else if (fClass.isEnum()) {
            fType = new EnumType<>(fClass);
        } else if (List.class.isAssignableFrom(fClass)) {
            var a = field.getAnnotation(PropertyElement.class);
            var eltClass = typeRegistry.load(a.value());
            fType = ListType.of(eltClass);
        } else if (Map.class.isAssignableFrom(fClass)) {
            var a = field.getAnnotation(PropertyElement.class);
            var eltClass = typeRegistry.load(a.value());
            fType = MapType.of(eltClass);
        } else {
            fType = typeRegistry.load(fClass);
        }

        Object devault = defaultValue(field, fType);
        return new ReflectProp<Object, Object>(propName, fType, devault, getter, setter);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static <T> Object defaultValue(Field field, Type<T> fType) {
        Class<T> fClass = (Class<T>) field.getType();

        if (fClass == Boolean.class || fClass == boolean.class) {
            var da = field.getAnnotation(DefaultBoolean.class);
            return da.value();
        }

        if (fClass == Class.class) {
            var da = field.getAnnotation(DefaultClass.class);
            return da.value();
        }

        if (fClass == Duration.class) {
            var da = field.getAnnotation(DefaultDuration.class);
            return DurationHelper.parse(da.value());
        }

        if (Enum.class.isAssignableFrom(fClass)) {
            var da = field.getAnnotation(DefaultEnum.class);
            var enumClass = (Class<Enum>) fClass;
            return Enum.valueOf(enumClass, da.value());
        }

        if (fClass == Integer.class || fClass == int.class) {
            var da = field.getAnnotation(DefaultInt.class);
            return da.value();
        }

        if (fClass == String.class) {
            var da = field.getAnnotation(Default.class);
            return da.value();
        }

        var da = field.getAnnotation(Default.class);
        var daValue = da.value();
        return fType.convert(null, daValue);
    }

}
