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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class PropertyHelper {

    public static Collection<Property<?>> buildProperties(Property<?>... array) {
        var list = Arrays.asList(array);
        return Collections.unmodifiableList(list);
    }

    public static Collection<Property<?>> buildProperties(Collection<Property<?>> supers, Property<?>... array) {
        var list = new ArrayList<Property<?>>(supers.size() + array.length);
        list.addAll(supers);
        list.addAll(Arrays.asList(array));
        return Collections.unmodifiableList(list);
    }

}
