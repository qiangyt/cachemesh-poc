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
package cachemesh.core.config;

import cachemesh.common.config.Prop;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Bean;
import cachemesh.common.config.op.StringOp;
import lombok.Getter;
import lombok.Setter;

public abstract class NodesConfig implements Bean {

    public static final NodesConfigOp OP = new NodesConfigOp();

    public static final Prop<String> KIND_PROP = Prop.<String> builder().config(NodesConfig.class).name("kind")
            .devault("inline").op(StringOp.DEFAULT).build();

    public static final Iterable<Prop<?>> PROPS = ConfigHelper.props(KIND_PROP);

    @Getter
    @Setter
    private String kind;

    @Override
    public Iterable<Prop<?>> props() {
        return PROPS;
    }

}
