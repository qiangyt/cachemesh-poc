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
package cachemesh.common.config.op;

import java.util.Map;

import cachemesh.common.config.Bean;
import lombok.Getter;

@Getter
public abstract class DynamicOp<T extends Bean> extends BeanOp<T> {

	public abstract BeanOp<? extends T> op(String hint, Map<String, Object> parent, Map<String, Object> value);

    @Override
    public T newBean(String hint, Map<String, Object> parent, Map<String, Object> value) {
        var targetOp = op(hint, parent, value);
        return targetOp.newBean(hint, parent, value);
    }

}
