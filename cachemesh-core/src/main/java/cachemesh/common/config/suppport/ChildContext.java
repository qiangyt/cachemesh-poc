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

import javax.annotation.Nonnull;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Path;
import lombok.Getter;
import static com.google.common.base.Preconditions.*;

@Getter
public class ChildContext extends AbstractContext {

    @Nonnull
    private final Path path;

    @Nonnull
    private final ConfigContext parent;

    @Nonnull
    private final ConfigContext root;

    public ChildContext(@Nonnull ConfigContext parent, @Nonnull String childName) {
        this(parent, Path.of(parent.getPath(), childName));
    }

    public ChildContext(@Nonnull ConfigContext parent, int childIndex) {
        this(parent, Path.of(parent.getPath(), childIndex));
    }

    private ChildContext(@Nonnull ConfigContext parent, @Nonnull Path path) {
        super(parent.getClassCache(), parent.getTypeRegistry(), parent.getRootValue());

        this.parent = parent;
        this.path = checkNotNull(path);
        this.root = parent.getRoot();
    }

}
