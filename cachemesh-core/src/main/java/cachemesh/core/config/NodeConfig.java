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

import java.net.MalformedURLException;

import cachemesh.common.config3.annotations.Property;
import cachemesh.common.config3.annotations.DefaultBoolean;
import cachemesh.common.config3.annotations.DefaultInt;
import cachemesh.common.config3.types.BooleanType;
import cachemesh.common.config3.types.IntegerType;
import cachemesh.common.misc.SimpleURL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class NodeConfig {

    public static final boolean DEFAULT_LOCAL = false;

    public static final int DEFAULT_START_TIMEOUT = 1;

    public static final int DEFAULT_STOP_TIMEOUT = 2;

    private SimpleURL url;

    // @Builder.Default
    @Property @DefaultBoolean(DEFAULT_LOCAL)
    private boolean local;

    // @Builder.Default
    @Property @DefaultInt(DEFAULT_START_TIMEOUT)
    private int startTimeout = DEFAULT_START_TIMEOUT;

    // @Builder.Default
    @Property @DefaultInt(DEFAULT_STOP_TIMEOUT)
    private int stopTimeout = DEFAULT_STOP_TIMEOUT;

    protected NodeConfig(SimpleURL url) {
        setUrl(url);
    }

    public NodeConfig(SimpleURL url, boolean local, int startTimeout, int stopTimeout) {
        this(url);

        this.local = local;
        this.startTimeout = startTimeout;
        this.stopTimeout = stopTimeout;
    }

    public NodeConfig(boolean local, int startTimeout, int stopTimeout) {
        this.local = local;
        this.startTimeout = startTimeout;
        this.stopTimeout = stopTimeout;
    }

    public String getProtocol() {
        return getUrl().getProtocol();
    }

    public abstract String getTarget();

    public SimpleURL getUrl() {
        if (this.url == null) {
            try {
                this.url = buildUrl();
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return this.url;
    }

    protected SimpleURL buildUrl() throws MalformedURLException {
        return new SimpleURL(String.format("%s://%s", getProtocol(), getTarget()));
    }

    public void setUrl(SimpleURL url) {
        var query = url.getQuery();

        if (query.containsKey("startTimeout")) {
            var startTimeout = IntegerType.DEFAULT.convert(null, query.get("startTimeout"));
            setStartTimeout(startTimeout);
        }

        if (query.containsKey("stopTimeout")) {
            var stopTimeout = IntegerType.DEFAULT.convert(null, query.get("stopTimeout"));
            setStopTimeout(stopTimeout);
        }

        if (query.containsKey("local")) {
            var local = BooleanType.DEFAULT.convert(null, query.get("local"));
            setLocal(local);
        }
    }

}
