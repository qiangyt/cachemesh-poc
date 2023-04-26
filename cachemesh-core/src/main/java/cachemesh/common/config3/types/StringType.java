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

import cachemesh.common.config3.ConvertContext;
import cachemesh.common.config3.suppport.AbstractType;

public class StringType extends AbstractType<String> {

    public static final StringType DEFAULT = new StringType();

    @Override
    public Class<?> getKlass() {
        return String.class;
    }

    @Override
    protected String doConvert(ConvertContext ctx, Object value) {
        return (String) value;
    }

}