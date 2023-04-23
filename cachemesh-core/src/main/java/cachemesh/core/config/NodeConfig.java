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

import cachemesh.common.config2.Mapper;
import cachemesh.common.config2.TypeRegistry;
import cachemesh.common.config2.annotations.Property;
import cachemesh.common.config2.reflect.ReflectDef;
import cachemesh.common.config2.reflect.ReflectMapper;
import cachemesh.common.config2.types.BooleanType;
import cachemesh.common.config2.types.IntegerType;
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

    //@Builder.Default
    @Property(devault = "false")
    private boolean local = DEFAULT_LOCAL;

    //@Builder.Default
    @Property(devault = "1")
    private int startTimeout = DEFAULT_START_TIMEOUT;

    //@Builder.Default
    @Property(devault = "2")
    private int stopTimeout = DEFAULT_STOP_TIMEOUT;

    /*public Mapper<NodeConfig> buildMapper(TypeRegistry typeRegistry) {
        var def = ReflectDef.of(typeRegistry, NodeConfig.class);
        return new ReflectMapper<>(def);
    }*/

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
            var startTimeout = IntegerType.DEFAULT.convert(null, null, null, "startTimeout");
            setStartTimeout(startTimeout);
        }

        if (query.containsKey("stopTimeout")) {
            var stopTimeout = IntegerType.DEFAULT.convert(null, null, null,"stopTimeout");
            setStopTimeout(stopTimeout);
        }

        if (query.containsKey("local")) {
            var local = BooleanType.DEFAULT.convert(null, null, null, "local");
            setLocal(local);
        }
    }

}
