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
package cachemesh.common.config3.types;

import cachemesh.common.config3.Type;
import cachemesh.common.config3.suppport.AbstractType;
import cachemesh.common.config3.Path;
import lombok.Getter;

@Getter
public abstract class ContainerType<T, E> extends AbstractType<T> {

    private final Type<E> elementType;

    private final Class<?> klass;

    protected ContainerType(Class<?> klass, Type<E> elementType) {
        this.klass = klass;
        this.elementType = elementType;
    }

    protected E convertElement(Path elementPath, Object elementValue) {
        return getElementType().convert(elementPath, elementValue);
    }

}
