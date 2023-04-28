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
package cachemesh.common.config;

import java.util.ArrayList;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

import lombok.Getter;

public class Path {

    public static final Path ROOT = new Path(null, "/") {

        {
            this.chain = new ArrayList<>(1);
            this.chain.add(this);
        }

        @Override
        public Path getParent() {
            return null;
        }

        @Override
        public boolean isKeep() {
            return false;
        }

        @Override
        public boolean isUpward() {
            return false;
        }

        @Override
        public boolean isAbsolute() {
            return true;
        }

        @Override
        public boolean isRoot() {
            return true;
        }

        @Override
        public ArrayList<Path> toChain() {
            return this.chain;
        }

        @Override
        public String toString() {
            return "/";
        }

        @Override
        public boolean isIndex() {
            return false;
        }
    };

    public static final Path KEEP = new Path(null, ".") {

        {
            this.chain = new ArrayList<>(1);
            this.chain.add(this);
        }

        @Override
        public Path getParent() {
            return null;
        }

        @Override
        public boolean isKeep() {
            return true;
        }

        @Override
        public boolean isUpward() {
            return false;
        }

        @Override
        public boolean isAbsolute() {
            return false;
        }

        @Override
        public boolean isRoot() {
            return false;
        }

        @Override
        public ArrayList<Path> toChain() {
            return this.chain;
        }

        @Override
        public String toString() {
            return ".";
        }

        @Override
        public boolean isIndex() {
            return false;
        }
    };

    @Getter
    @Nullable
    private final Path parent;

    @Getter
    @Nonnull
    private final String name;

    @Nullable
    private String str;

    @Nullable
    protected ArrayList<Path> chain;

    @Getter
    private final boolean absolute;

    @Getter
    private int index;

    private Path(@Nullable Path parent, @Nonnull String name) {
        this.parent = parent;
        this.name = checkNotNull(name);

        if (parent == null) {
            this.absolute = isRoot();
        } else {
            this.absolute = parent.isAbsolute();
        }

        this.index = -1;
    }

    private Path(Path parent, int index) {
        this(parent, String.format("[%d]", index));

        checkArgument(index >= 0);
        this.index = index;
    }

    public boolean isRoot() {
        return false;
    }

    @Nonnull
    public ArrayList<Path> toChain() {
        ArrayList<Path> r = this.chain;
        if (r != null) {
            return r;
        }

        var prt = getParent();
        if (prt != null) {
            r = prt.toChain();
        } else {
            this.chain = r = new ArrayList<>();
        }

        r.add(this);
        return r;
    }

    public boolean isKeep() {
        return ".".equals(getName());
    }

    public boolean isUpward() {
        return "..".equals(getName());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }

        try {
            return toString().equals(((Path) object).toString());
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        if (this.str != null) {
            return this.str;
        }

        var p = getParent();
        var n = getName();

        StringBuilder sb;
        if (p == ROOT) {
            sb = new StringBuilder(1 + n.length());
        } else {
            var ps = p.toString();
            sb = new StringBuilder(ps.length() + 1 + n.length());
            sb.append(ps);
        }
        sb.append('/').append(n);

        return this.str = sb.toString();
    }

    public boolean isIndex() {
        return getIndex() >= 0;
    }

    @Nonnull
    public static Path of(int index) {
        checkArgument(index >= 0);
        return of(KEEP, index);
    }

    @Nonnull
    public static Path of(@Nonnull String path) {
        return of(KEEP, path);
    }

    @Nonnull
    public static Path of(@Nonnull Path current, int index) {
        checkArgument(index >= 0);
        return new Path(current, index);
    }

    @Nonnull
    public static Path of(@Nonnull Path current, @Nonnull String path) {
        checkNotNull(current);
        checkNotNull(path);

        int len = path.length();

        if (len == 0) {
            return current;
        }
        if (len == 1) {
            switch (path.charAt(0)) {
            case '.':
                return current;
            case '/':
                return ROOT;
            default:
                return new Path(current, path);
            }
        }

        var pos = path.lastIndexOf('/');
        if (pos < 0) {
            return new Path(current, path);
        }
        if (pos == 0) {
            return of(ROOT, path.substring(1));
        }

        var parent = of(current, path.substring(0, pos));
        if (pos == len - 1) {
            return parent;
        }

        return of(parent, path.substring(pos + 1));
    }

}
