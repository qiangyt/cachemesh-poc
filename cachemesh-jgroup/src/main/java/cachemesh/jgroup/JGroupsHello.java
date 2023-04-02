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

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.util.Util;

public class JGroupsHello {

    protected JChannel ch;

    public void start(String name) throws Exception {
        this.ch = new JChannel("jgroups.xml").name(name).setReceiver(new MyReceiver(name)).connect("demo-cluster");
        int counter = 1;
        for (;;) {
            this.ch.send(null, "msg-" + counter++);
            Util.sleep(3000);
        }
    }

    public void close() {
        this.ch.close();
    }

    protected static class MyReceiver implements Receiver {
        protected final String name;

        protected MyReceiver(String name) {
            this.name = name;
        }

        public void receive(Message msg) {
            System.out.printf("-- [%s] msg from %s: %s\n", name, msg.src(), msg.getObject());
        }

        public void viewAccepted(View v) {
            System.out.printf("-- [%s] new view: %s\n", name, v);
        }
    }

    public static void main(String[] args) throws Exception {
        new JGroupsHello().start(args[0]);
    }
}
