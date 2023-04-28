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
package cachemesh.discovery.jgroup;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.util.Util;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

public class JGroupsHello {

    @Nonnull
    protected JChannel ch;

    @Nonnull
    public void start(@Nonnull String name) throws Exception {
        checkNotNull(name);

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
        @Nonnull
        protected final String name;

        protected MyReceiver(@Nonnull String name) {
            checkNotNull(name);

            this.name = name;
        }

        public void receive(@Nonnull Message msg) {
            checkNotNull(msg);

            System.out.printf("-- [%s] msg from %s: %s\n", name, msg.src(), msg.getObject());
        }

        public void viewAccepted(@Nonnull View v) {
            checkNotNull(v);

            System.out.printf("-- [%s] new view: %s\n", name, v);
        }
    }

    public static void main(String[] args) throws Exception {
        new JGroupsHello().start(args[0]);
    }
}
