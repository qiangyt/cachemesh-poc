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

public class Path {

    public static final Path ROOT = new Path(null, "/") {
        @Override
        public Path parent() {
            throw new IllegalArgumentException("root path has no parent");
        }

        @Override
        public String toString() {
            return "/";
        }
    };

    public static final Path KEEP = new Path(null, ".") {
        @Override
        public String toString() {
            return ".";
        }
    };

    private final Path parent;

    private final String name;

    private String str;

    private Path(Path parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public boolean isRoot() {
        return this == ROOT;
    }

    public boolean isKeep() {
        return this == KEEP;
    }

    public boolean isBackward() {
        return "..".equals(name());
    }

    public Path parent() {
        return this.parent;
    }

    public String name() {
        return this.name;
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

        var p = parent();
        var n = name();

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

    public static Path of(String path) {
        return of(KEEP, path);
    }

    public static Path of(Path current, String path) {
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
