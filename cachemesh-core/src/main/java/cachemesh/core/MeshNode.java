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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import cachemesh.common.hash.HasKey;
import cachemesh.common.misc.LifeStage;
import cachemesh.common.misc.LogHelper;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.spi.NodeHook;
import cachemesh.core.spi.Transport;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class MeshNode implements HasKey {

    protected final Logger logger;

    private final NodeConfig config;

    private final int hashCode;

    private final Transport transport;

    @Getter(AccessLevel.PROTECTED)
    private final List<NodeHook> hooks = new ArrayList<>();

    private final LifeStage lifeStage;

    public MeshNode(NodeConfig config, Transport transport) {
        this.config = config;
        this.transport = transport;

        var key = getKey();

        this.hashCode = key.hashCode();
        this.logger = LogHelper.getLogger(getClass(), key);

        this.lifeStage = new LifeStage("meshnode", key, getLogger());
    }

    @Override
    public String getKey() {
        return getConfig().getTarget();
    }

    public void addHook(NodeHook hook) {
        getHooks().add(hook);
    }

    void beforeStart() throws InterruptedException {
        getLifeStage().starting();

        int timeout = getConfig().getStartTimeout();
        for (var hook : getHooks()) {
            hook.beforeNodeStart(this, timeout);
        }
    }

    void afterStart() throws InterruptedException {
        getLifeStage().started();

        int timeout = getConfig().getStartTimeout();
        for (var hook : getHooks()) {
            hook.afterNodeStart(this, timeout);
        }
    }

    void beforeStop() throws InterruptedException {
        getLifeStage().stopping();

        int timeout = getConfig().getStopTimeout();
        for (var hook : getHooks()) {
            hook.beforeNodeStop(this, timeout);
        }
    }

    void afterStop() throws InterruptedException {
        getLifeStage().stopped();

        int timeout = getConfig().getStopTimeout();
        for (var hook : getHooks()) {
            hook.afterNodeStop(this, timeout);
        }
    }

    public void start() throws InterruptedException {
        beforeStart();

        int timeout = getConfig().getStopTimeout();
        getTransport().start(timeout);

        afterStart();
    }

    public void stop() throws InterruptedException {
        beforeStop();

        int timeout = getConfig().getStopTimeout();
        getTransport().stop(timeout);

        afterStop();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        MeshNode that;
        try {
            that = (MeshNode) obj;
        } catch (ClassCastException e) {
            return false;
        }

        return getKey().equals(that.getKey());
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return getKey() + "@" + (getConfig().isLocal() ? "local" : "remote");
    }

}
