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
package cachemesh.jgroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.err.MeshException;

public class MessageReceiver implements Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);

    // private View currentView;

    private final JGroupsMembers members;

    private final JGroupsListener listener;

    private final Map<String, String> nodes = new ConcurrentHashMap<>();

    MessageReceiver(JGroupsMembers members, JGroupsListener listener) {
        this.members = members;
        this.listener = listener;
    }

    @Override
    public void receive(Message msg) {
        LOG.info("got message. from={}", msg.getSrc());

        String text = msg.getObject();
        if (text == null) {
            LOG.warn("got invalid message (reason: payload is null). from={}, text={}", msg.getSrc(), text);
            return;
        }
        int pos = text.indexOf(" ");
        if (pos <= 0 || pos == text.length() - 1) {
            LOG.warn("got invalid message (reason: type header not found). from={}, text={}", msg.getSrc(), text);
            return;
        }

        String type = text.substring(0, pos);
        String body = text.substring(pos + 1);

        try {
            if (type.equals(NodeMessageType.NodeJoin.name())) {
                var nodeUrl = body;
                if (this.members.getUrl().equals(nodeUrl) == false) {
                    nodes.computeIfAbsent(nodeUrl, k -> {
                        try {
                            this.listener.onNodeJoin(nodeUrl);
                            return nodeUrl;
                        } catch (RuntimeException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new MeshException(e);
                        }
                    });
                }
            } else if (type.equals(NodeMessageType.NodeLeave.name())) {
                var nodeUrl = body;
                if (this.members.getUrl().equals(nodeUrl) == false) {
                    nodes.computeIfPresent(nodeUrl, (k, v) -> {
                        try {
                            this.listener.onNodeLeave(body);
                            return null;
                        } catch (RuntimeException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new MeshException(e);
                        }
                    });
                }
            } else {
                LOG.warn("got invalid message (reason: unexpected type. from={}, text={}", msg.getSrc(), text);
                return;
            }
        } catch (Exception e) {
            String errMsg = String.format("error occurred when handling message. from=%s, text=%s", msg.getSrc(), text);
            LOG.error(errMsg, e);
        }
    }

    @Override
    public void viewAccepted(View v) {
        // this.currentView = v;
    }

}
