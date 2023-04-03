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
package cachemesh.test;

import cachemesh.core.MeshNetwork;
import cachemesh.core.config.MeshConfig;
import cachemesh.jgroup.JGroupsListener;
import cachemesh.jgroup.JGroupsMembers;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("usage: <say-hello> <url>");
            return;
        }

        boolean sayHello = "true".equals(args[0]);
        System.out.printf("sayHello=%s\n", sayHello);

        String primaryUrl = args[1];

        System.out.println("mesh bootstrap: ...");

        var configYamlStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("example.yaml");
        var config = MeshConfig.fromYaml(configYamlStream);
        var mesh = MeshNetwork.build(config);

        mesh.start();

        Thread.sleep(5000);
        System.out.println("mesh bootstrap: done");

        var jgroups = new JGroupsMembers(primaryUrl, mesh.getName(), "jgroups.xml", new JGroupsListener() {
            @Override
            public void onNodeJoin(String nodeUrl) throws Exception {
                System.out.println("join " + nodeUrl);
                mesh.addRemoteNode(nodeUrl);
            }

            @Override
            public void onNodeLeave(String nodeUrl) throws Exception {
                System.out.println("leave " + nodeUrl);
                // mesh.remoteNode(nodeUrl);
            }
        });
        jgroups.start();

        // String[] nodeUrls = args;

        try {
            if (!sayHello) {
                for (;;) {
                    Thread.sleep(100);
                }
            } else {
                var cache = mesh.resolveCache("example", String.class);

                for (int i = 0; i < 100; i++) {
                    Thread.sleep(2000);

                    String key = "k" + i;
                    String v = "v" + i;
                    System.out.printf("putSingle('%s', '%s')\n", key, v);
                    cache.putSingle(key, v);
                }

                for (int i = 0; i < 100; i++) {
                    Thread.sleep(2000);

                    String key = "k" + i;
                    String v = cache.getSingle(key);
                    System.out.printf("getSingle('%s') returns: %s\n", key, v);
                }

                for (int i = 0; i < 100; i++) {
                    Thread.sleep(2000);

                    String key = "k" + i;
                    String v = "v" + (i * 2);
                    System.out.printf("putSingle('%s', '%s')\n", key, v);
                    cache.putSingle(key, v);
                }

                for (int i = 0; i < 100; i++) {
                    Thread.sleep(2000);

                    String key = "k" + i;
                    String v = cache.getSingle(key);
                    System.out.printf("getSingle('%s') returns: %s\n", key, v);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            jgroups.stop();
            mesh.shutdown(0);
        }
    }

}
