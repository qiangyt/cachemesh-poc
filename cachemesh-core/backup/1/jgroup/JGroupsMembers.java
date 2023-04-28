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
package cachemesh.jgroup;

import org.jgroups.JChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.err.BadStateException;
import lombok.Getter;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
public class JGroupsMembers {

    @Nonnull
    private final JGroupsListener listener;

    @Nonnull
    private final String url;

    @Nonnull
    private final String meshNetworkName;

    @Nonnull
    private final String configXmlClasspath;

    @Nonnull
    private final Logger log;

    @Nonnull
    private JChannel channel;

    public JGroupsMembers(@Nonnull String url, @Nonnull String meshNetworkName, @Nonnull String configXmlClasspath,
            @Nonnull JGroupsListener listener) {
        checkNotNull(url);
        checkNotNull(meshNetworkName);
        checkNotNull(configXmlClasspath);
        checkNotNull(listener);

        this.listener = listener;
        this.url = url;
        this.meshNetworkName = meshNetworkName;
        this.configXmlClasspath = configXmlClasspath;

        this.log = LoggerFactory.getLogger(toString());
    }

    @Override
    @Nonnull
    public String toString() {
        return this.meshNetworkName + "=" + this.url;
    }

    public void start() {
        this.log.info("connect to mesh {} ....", this.meshNetworkName);

        try {
            var ch = new JChannel(this.configXmlClasspath);
            ch.name(this.url).setReceiver(new MessageReceiver(this, this.listener));

            this.channel = ch.connect(this.meshNetworkName);
            this.log.info("connect to mesh {}: done", this.meshNetworkName);

            ch.send(null, NodeMessageType.NodeJoin.name() + " " + this.url);
            this.log.info("sent node join message. url={}", this.url);
        } catch (Exception e) {
            throw new BadStateException(e, "failed to join the mesh: %s", toString());
        }
    }

    public void stop() {
        var ch = this.channel;
        if (ch != null) {
            this.log.info("leave the mesh: ...");

            try {
                ch.send(null, NodeMessageType.NodeLeave.name() + " " + this.url);
                this.log.info("sent node leave message. url={}", this.url);
            } catch (Exception e) {
                this.log.error("", "error occurred during leaveing the mesh", e);
            }

            ch.close();
            this.log.info("leave the mesh: done");
        }
    }

}
