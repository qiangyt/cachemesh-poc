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
package cachemesh.lettuce;

import java.net.MalformedURLException;

import cachemesh.common.config.ReflectProp;
import cachemesh.common.config.Prop;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.op.IntegerOp;
import cachemesh.common.config.op.ReflectOp;
import cachemesh.common.config.op.StringOp;
import cachemesh.common.misc.SimpleURL;
import cachemesh.core.config.NodeConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LettuceConfig extends NodeConfig {

    public static final ReflectOp<LettuceConfig> OP = new ReflectOp<>(LettuceConfig.class);

    public static final String PROTOCOL = "lettuce";

    public static final int DEFAULT_PORT = 6379;

    public static final int DEFAULT_DATABASE = 0;

    public static final String DEFAULT_SEPARATOR = "%";

    public static final Prop<String> HOST_PROP = ReflectProp.<String> builder().config(LettuceConfig.class).name("host")
            .op(StringOp.DEFAULT).build();

    public static final Prop<Integer> PORT_PROP = ReflectProp.<Integer> builder().config(LettuceConfig.class).name("port")
            .devault(DEFAULT_PORT).op(IntegerOp.DEFAULT).build();

    public static final Prop<Integer> DATABASE_PROP = ReflectProp.<Integer> builder().config(LettuceConfig.class)
            .name("database").devault(DEFAULT_DATABASE).op(IntegerOp.DEFAULT).build();

    public static final Prop<String> KEY_SEPARATOR_PROP = ReflectProp.<String> builder().config(LettuceConfig.class)
            .name("keySeparator").devault(DEFAULT_SEPARATOR).op(StringOp.DEFAULT).build();

    public static final Iterable<Prop<?>> PROPS = ConfigHelper.props(NodeConfig.PROPS, HOST_PROP, PORT_PROP,
            DATABASE_PROP, KEY_SEPARATOR_PROP);

    private String target;

    private String host;

    private int port;

    private int database;

    private String keySeparator;

    @Builder
    public LettuceConfig(SimpleURL url) {
        super(url);
    }

    @Builder
    public LettuceConfig(SimpleURL url, int database, String keySeparator, boolean local, int startTimeout,
            int stopTimeout) {
        super(url, local, startTimeout, stopTimeout);

        this.database = database;
        this.keySeparator = keySeparator;
    }

    @Builder
    public LettuceConfig(String host, int port, int database, String keySeparator, boolean local, int startTimeout,
            int stopTimeout) {
        super(local, startTimeout, stopTimeout);

        this.database = database;
        this.keySeparator = keySeparator;
    }

    @Override
    public Iterable<Prop<?>> props() {
        return PROPS;
    }

    public String getTarget() {
        if (this.target == null) {
            this.target = String.format("%s:%d", getHost(), getPort());
        }
        return this.target;
    }

    @Override
    public void setUrl(SimpleURL url) {
        super.setUrl(url);

        setHost(url.getHost());
        setPort(url.getPort());

        var q = url.getQuery();

        if (q.containsKey("database")) {
            setDatabase(IntegerOp.DEFAULT.populate("", null, q.get("database")));
        }
        if (q.containsKey("keySeparator")) {
            setKeySeparator(q.get("keySeparator"));
        }
    }

    @Override
    protected SimpleURL buildUrl() throws MalformedURLException {
        return new SimpleURL(String.format("%s://%s/%d", getProtocol(), getTarget(), getDatabase()));
    }

}
