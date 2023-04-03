/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import cachemesh.common.LifeStage;
import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.util.LogHelper;
import cachemesh.core.spi.Transport;
import cachemesh.core.spi.NodeHook;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class MeshNode implements ConsistentHash.Node {

    protected final Logger logger;

    private final TransportConfig config;

    private final int hashCode;

    private final Transport transport;

    @Getter(AccessLevel.PROTECTED)
    private final List<NodeHook> hooks = new ArrayList<>();

    private final LifeStage lifeStage;

    public MeshNode(TransportConfig config, Transport transport) {
        this.config = config;
        this.transport = transport;

        var key = getKey();

        this.hashCode = key.hashCode();
        this.logger = LogHelper.getLogger(this);

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

        int timeout = getConfig().getStartTimeoutSeconds();
        for (var hook : getHooks()) {
            hook.beforeNodeStart(this, timeout);
        }
    }

    void afterStart() throws InterruptedException {
        getLifeStage().started();

        int timeout = getConfig().getStartTimeoutSeconds();
        for (var hook : getHooks()) {
            hook.afterNodeStart(this, timeout);
        }
    }

    void beforeStop() throws InterruptedException {
        getLifeStage().stopping();

        int timeout = getConfig().getStopTimeoutSeconds();
        for (var hook : getHooks()) {
            hook.beforeNodeStop(this, timeout);
        }
    }

    void afterStop() throws InterruptedException {
        getLifeStage().stopped();

        int timeout = getConfig().getStopTimeoutSeconds();
        for (var hook : getHooks()) {
            hook.afterNodeStop(this, timeout);
        }
    }

    public void start() throws InterruptedException {
        beforeStart();

        int timeout = getConfig().getStopTimeoutSeconds();
        getTransport().start(timeout);

        afterStart();
    }

    public void stop() throws InterruptedException {
        beforeStop();

        int timeout = getConfig().getStopTimeoutSeconds();
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
        return getKey() + "@" + (getConfig().isRemote() ? "remote" : "local");
    }

}
