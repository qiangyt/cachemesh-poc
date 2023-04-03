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
package cachemesh.core;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.Mappable;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class TransportConfig implements Mappable {

    public static final int DEFAULT_START_TIMEOUT_SECONDS = 1;

    private int startTimeoutSeconds = DEFAULT_START_TIMEOUT_SECONDS;

    public static final int DEFAULT_STOP_TIMEOUT_SECONDS = 2;

    private int stopTimeoutSeconds = DEFAULT_STOP_TIMEOUT_SECONDS;

    public abstract String getProtocol();

    public abstract boolean isRemote();

    @Override
    public Map<String, Object> toMap() {
        var configMap = new HashMap<String, Object>();

        configMap.put("startTimeoutSeconds", getStartTimeoutSeconds());
        configMap.put("stopTimeoutSeconds", getStopTimeoutSeconds());
        configMap.put("remote", isRemote());
        configMap.put("protocol", getProtocol());
        configMap.put("target", getTarget());

        return configMap;
    }

    public abstract String getTarget();

}
