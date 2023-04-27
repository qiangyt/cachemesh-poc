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
package cachemesh.common.config.types;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cachemesh.common.annotation.ADefault;
import cachemesh.common.annotation.ADefaultBoolean;
import cachemesh.common.annotation.ADefaultClass;
import cachemesh.common.annotation.ADefaultDuration;
import cachemesh.common.annotation.ADefaultEnum;
import cachemesh.common.annotation.ADefaultInt;
import cachemesh.common.annotation.AElement;
import cachemesh.common.annotation.AIgnored;
import cachemesh.common.annotation.AProperty;
import cachemesh.common.config.Property;
import cachemesh.common.config.Type;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.suppport.AbstractProperty;
import cachemesh.common.misc.DurationHelper;
import cachemesh.common.misc.Reflect;
import lombok.Getter;

@Getter
public class ReflectProperty<B, T> extends AbstractProperty<B, T> {

    private final Method setter;

    private final Method getter;

    protected ReflectProperty(String name, Type<T> fieldType, T devault, Method getter, Method setter) {
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
    public static <B> Map<String, Property<B, ?>> of(TypeRegistry typeRegistry, Class<B> klass) {
        var r = new HashMap<String, Property<B, ?>>();

        for (var f : klass.getDeclaredFields()) {
            var p = (Property<B, ?>) ReflectProperty.of(typeRegistry, f);
            if (p != null) {
                r.put(p.getName(), p);
            }
        }

        return r;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ReflectProperty<?, ?> of(TypeRegistry typeRegistry, Field field) {
        String propName = null;
        String setterName = null;
        String getterName = null;

        if (field.getAnnotation(AIgnored.class) != null) {
            return null;
        }

        var pa = field.getAnnotation(AProperty.class);
        if (pa != null) {
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
            fType = ArrayType.of(typeRegistry, (Class<Object[]>) fClass);
        } else if (fClass.isEnum()) {
            fType = new EnumType<>(fClass);
        } else if (List.class.isAssignableFrom(fClass)) {
            var a = field.getAnnotation(AElement.class);
            var eltClass = typeRegistry.load(a.value());
            fType = ListType.of(eltClass);
        } else if (Map.class.isAssignableFrom(fClass)) {
            var a = field.getAnnotation(AElement.class);
            var eltClass = typeRegistry.load(a.value());
            fType = MapType.of(eltClass);
        } else {
            fType = typeRegistry.load(fClass);
        }

        Object devault = defaultValue(field, fType);
        return new ReflectProperty<Object, Object>(propName, fType, devault, getter, setter);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static <T> Object defaultValue(Field field, Type<T> fType) {
        Class<T> fClass = (Class<T>) field.getType();

        if (fClass == Boolean.class || fClass == boolean.class) {
            var da = field.getAnnotation(ADefaultBoolean.class);
            return da.value();
        }

        if (fClass == Class.class) {
            var da = field.getAnnotation(ADefaultClass.class);
            return da.value();
        }

        if (fClass == Duration.class) {
            var da = field.getAnnotation(ADefaultDuration.class);
            return DurationHelper.parse(da.value());
        }

        if (Enum.class.isAssignableFrom(fClass)) {
            var da = field.getAnnotation(ADefaultEnum.class);
            var enumClass = (Class<Enum>) fClass;
            return Enum.valueOf(enumClass, da.value());
        }

        if (fClass == Integer.class || fClass == int.class) {
            var da = field.getAnnotation(ADefaultInt.class);
            return da.value();
        }

        if (fClass == String.class) {
            var da = field.getAnnotation(ADefault.class);
            return da.value();
        }

        var da = field.getAnnotation(ADefault.class);
        var daValue = da.value();
        return fType.convert(null, daValue);
    }

}
