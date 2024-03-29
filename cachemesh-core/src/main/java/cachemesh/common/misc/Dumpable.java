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
package cachemesh.common.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

public interface Dumpable {

    @Nonnull
    Map<String, Object> toMap();

    @Nullable
    static List<Map<String, Object>> toMap(@Nullable List<? extends Dumpable> list) {
        if (list == null) {
            return null;
        }

        var r = new ArrayList<Map<String, Object>>(list.size());

        for (var element : list) {
            r.add(element.toMap());
        }

        return r;
    }

}
