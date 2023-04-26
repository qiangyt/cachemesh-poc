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
package cachemesh.common.config.suppport;

import cachemesh.common.config.ConvertContext;
import cachemesh.common.config.Path;
import lombok.Getter;

@Getter
public class ChildConvertContext extends AbstractConvertContext {

    private final Path path;

    private final ConvertContext parent;

    private final ConvertContext root;

    public ChildConvertContext(ConvertContext parent, String childName) {
        this(parent, Path.of(parent.getPath(), childName));
    }

    public ChildConvertContext(ConvertContext parent, int childIndex) {
        this(parent, Path.of(parent.getPath(), childIndex));
    }

    private ChildConvertContext(ConvertContext parent, Path path) {
        super(parent.getClassCache(), parent.getTypeRegistry(), parent.getRootValue());

        this.parent = parent;
        this.path = path;
        this.root = parent.getRoot();
    }

}
