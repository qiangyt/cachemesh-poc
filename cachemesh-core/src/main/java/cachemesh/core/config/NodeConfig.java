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
import java.util.Collection;

import cachemesh.common.config.BooleanOp;
import cachemesh.common.config.IntegerOp;
import cachemesh.common.config.NestedOp;
import cachemesh.common.config.NestedStaticOp;
import cachemesh.common.config.Property;
import cachemesh.common.config.SimpleUrlOp;
import cachemesh.common.config.SomeConfig;
import cachemesh.common.util.SimpleURL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class NodeConfig implements SomeConfig {

    public static final NestedOp<NodeConfig> OP = new NestedStaticOp<>(NodeConfig.class);

    public static final boolean DEFAULT_LOCAL = false;

    public static final int DEFAULT_START_TIMEOUT = 1;

    public static final int DEFAULT_STOP_TIMEOUT = 2;

    private SimpleURL url;

    // @Builder.Default
    private boolean local = DEFAULT_LOCAL;

    // @Builder.Default
    private int startTimeout = DEFAULT_START_TIMEOUT;

    // @Builder.Default
    private int stopTimeout = DEFAULT_STOP_TIMEOUT;

    public static final Collection<Property<?>> PROPERTIES = SomeConfig.buildProperties(
            Property.builder().config(NodeConfig.class).name("url").op(SimpleUrlOp.DEFAULT).build(),
            Property.builder().config(NodeConfig.class).name("local").devault(DEFAULT_LOCAL).op(BooleanOp.DEFAULT)
                    .build(),
            Property.builder().config(NodeConfig.class).name("startTimeout").op(IntegerOp.DEFAULT).build(),
            Property.builder().config(NodeConfig.class).name("stopTimeout").op(IntegerOp.DEFAULT).build());

    public NodeConfig(SimpleURL url) {
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

    public abstract String getProtocol();

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
    }

    public abstract String getTarget();

    public SimpleURL getUrl() {
        if (this.url == null) {
            try {
                this.url = buildUrl();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
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
            var startTimeout = IntegerOp.DEFAULT.convert("", query.get("startTimeout"));
            setStartTimeout(startTimeout);
        }

        if (query.containsKey("stopTimeout")) {
            var stopTimeout = IntegerOp.DEFAULT.convert("", query.get("stopTimeout"));
            setStopTimeout(stopTimeout);
        }

        if (query.containsKey("local")) {
            var local = BooleanOp.DEFAULT.convert("", query.get("local"));
            setLocal(local);
        }
    }

    public static NodeConfig fromUrl(String url) {
        return NodeConfigOp.DEFAULT.convert("", url);
    }

}
