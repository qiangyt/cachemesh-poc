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
package cachemesh.common.config2.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cachemesh.common.config2.AbstractProp;
import cachemesh.common.config2.Prop;
import cachemesh.common.config2.Type;
import cachemesh.common.config2.TypeRegistry;
import cachemesh.common.config2.annotations.IgnoredProperty;
import cachemesh.common.config2.annotations.Property;
import cachemesh.common.config2.annotations.PropertyElement;
import cachemesh.common.config2.types.EnumType;
import cachemesh.common.config2.types.ListType;
import cachemesh.common.config2.types.MapType;
import cachemesh.common.misc.Reflect;
import cachemesh.common.misc.StringHelper;

public class ReflectProp<T> extends AbstractProp<T> {

    private final Method setter;

    private final Method getter;

    protected ReflectProp(String name, Type<T> fieldType, T devault, Method getter, Method setter) {
        super(name, fieldType, devault);
        this.getter = getter;
        this.setter = setter;
    }

    public Method setter() {
        return this.setter;
    }

    public Method getter() {
        return this.getter;
    }

    @Override
    public void set(Object bean, Object value) {
        Reflect.set(setter(), bean, value);
    }

    public static Map<String, Prop<?>> of(TypeRegistry typeRegistry, Class<?> klass) {
        var r = new HashMap<String, Prop<?>>();

        for (var f : klass.getDeclaredFields()) {
            var p = ReflectProp.of(typeRegistry, f);
            r.put(p.name(), p);
        }

        return r;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ReflectProp<Object> of(TypeRegistry typeRegistry, Field field) {
        Class fClass = field.getType();
        var beanClass = field.getDeclaringClass();

        String propName = null;
        String devaultText = "";
        String setterName = null;
        String getterName = null;

        if (field.getAnnotation(IgnoredProperty.class) != null) {
            return null;
        }

        var pa = field.getAnnotation(Property.class);
        if (pa != null) {
            propName = pa.value();
            devaultText = pa.devault();
            setterName = pa.setter();
            getterName = pa.getter();
        }

        // name
        if (StringHelper.isBlank(propName)) {
            propName = field.getName().toLowerCase();
        }
        propName = propName.trim();
        String propCapName = StringHelper.capitalize(propName);

        // setter
        if (StringHelper.isBlank(setterName)) {
            setterName = "set" + propCapName;
        }
        setterName = setterName.trim();
        Method setter = Reflect.method(beanClass, setterName, fClass);
        if (setter == null) {
            var msg = String.format("failed to find setter method '%s' for property '%s.%s'", setterName, beanClass,
                    propName);
            throw new IllegalArgumentException(msg);
        }
        setter.setAccessible(true);

        // getter
        if (StringHelper.isBlank(getterName)) {
            getterName = "get" + propCapName;
        }
        getterName = getterName.trim();
        Method getter = Reflect.method(beanClass, getterName);
        if (getter == null) {
            if (fClass == boolean.class || fClass == Boolean.class) {
                getterName = "is" + propCapName;
                getter = Reflect.method(beanClass, getterName);
            }
            if (getter == null) {
                var msg = String.format("failed to find getter '%s' for property '%s.%s'", getterName, beanClass,
                        propName);
                throw new IllegalArgumentException(msg);
            }
        }
        if (getter.getReturnType() != fClass) {
            var msg = String.format("expect getter '%s' returns %s for property '%s.%s', but got %s", getterName,
                    beanClass, propName, getter.getReturnType());
            throw new IllegalArgumentException(msg);
        }
        getter.setAccessible(true);

        // create
        Type fType;
        if (fClass.isArray()) {
            fType = null;// TODO: ArrayType.of(typeRegistry, fClass);
        } else if (fClass.isEnum()) {
            fType = new EnumType<>(fClass);
        } else if (List.class.isAssignableFrom(fClass) || Map.class.isAssignableFrom(fClass)) {
            var a = field.getAnnotation(PropertyElement.class);

            var eltClass = typeRegistry.load(a.value());
            if (List.class.isAssignableFrom(fClass)) {
                fType = ListType.of(eltClass);
            } else {
                fType = MapType.of(eltClass);
            }
        } else {
            fType = typeRegistry.loadByKey(propCapName);
        }

        Object devault = null;
        if (!devaultText.isEmpty()) {
            devault = fType.convert(null, null, null, devaultText);
        }

        return new ReflectProp<Object>(propName, fType, devault, getter, setter);
    }

}
