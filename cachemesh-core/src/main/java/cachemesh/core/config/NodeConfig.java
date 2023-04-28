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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

import cachemesh.common.annotation.ADefaultBoolean;
import cachemesh.common.annotation.ADefaultInt;
import cachemesh.common.annotation.AProperty;
import cachemesh.common.config.types.BooleanType;
import cachemesh.common.config.types.IntegerType;
import cachemesh.common.err.BadValueException;
import cachemesh.common.misc.Dumpable;
import cachemesh.common.misc.SimpleURL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class NodeConfig implements Dumpable {

    public static final boolean DEFAULT_LOCAL = false;

    public static final int DEFAULT_START_TIMEOUT = 1;

    public static final int DEFAULT_STOP_TIMEOUT = 2;

    @Nonnull
    private SimpleURL url;

    // @Builder.Default
    @AProperty
    @ADefaultBoolean(DEFAULT_LOCAL)
    private boolean local;

    // @Builder.Default
    @AProperty
    @ADefaultInt(DEFAULT_START_TIMEOUT)
    private int startTimeout = DEFAULT_START_TIMEOUT;

    // @Builder.Default
    @AProperty
    @ADefaultInt(DEFAULT_STOP_TIMEOUT)
    private int stopTimeout = DEFAULT_STOP_TIMEOUT;

    protected NodeConfig(@Nonnull SimpleURL url) {
        checkNotNull(url);

        setUrl(url);
    }

    public NodeConfig(@Nonnull SimpleURL url, boolean local, int startTimeout, int stopTimeout) {
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

    @Nonnull
    public String getProtocol() {
        return getUrl().getProtocol();
    }

    @Nonnull
    public abstract String getTarget();

    @Nonnull
    public SimpleURL getUrl() {
        if (this.url == null) {
            try {
                this.url = buildUrl();
            } catch (MalformedURLException e) {
                throw new BadValueException(e);
            }
        }
        return this.url;
    }

    @Nonnull
    protected SimpleURL buildUrl() throws MalformedURLException {
        return new SimpleURL(String.format("%s://%s", getProtocol(), getTarget()));
    }

    public void setUrl(@Nonnull SimpleURL url) {
        checkNotNull(url);

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

    @Override
    @Nonnull
    public Map<String, Object> toMap() {
        var r = new HashMap<String, Object>();

        r.put("url", getUrl());
        r.put("local", isLocal());
        r.put("startTimeout", getStartTimeout());
        r.put("stopTimeout", getUrl());
        r.put("protocol", getProtocol());
        r.put("target", getTarget());

        return r;
    }

    @Override
    public int hashCode() {
        return getUrl().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        NodeConfig that;
        try {
            that = (NodeConfig) obj;
        } catch (ClassCastException e) {
            return false;
        }

        return getUrl().equals(that.getUrl());
    }

}
