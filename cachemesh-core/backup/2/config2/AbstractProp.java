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
package cachemesh.common.config2;

public abstract class AbstractProp<T> implements Prop<T> {

    private final String name;

    private final T devault;

    private final Type<T> type;

    public AbstractProp(String name, Type<T> type, T devault) {
        this.name = name;
        this.type = type;
        this.devault = devault;
    }

    public String name() {
        return this.name;
    }

    public T devault() {
        return this.devault;
    }

    public Type<T> type() {
        return this.type;
    }

}
