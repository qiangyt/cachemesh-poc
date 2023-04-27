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
package cachemesh.common.shutdown;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cachemesh.common.err.BadStateException;

public class ShutdownManager {

    public static final int DEFAULT_TIMEOUT_SECONDS = 2;

    public static final ShutdownManager DEFAULT = new ShutdownManager();

    private LinkedHashMap<String, ShutdownItem> items = new LinkedHashMap<>();

    @lombok.Getter
    private int maxTimeoutSeconds;

    private Thread thread;

    private final ShutdownLogger logger = new ShutdownLogger(ShutdownManager.class, "default");

    public void register(ManagedShutdownable target) {
        register(target, DEFAULT_TIMEOUT_SECONDS);
    }

    public void register(Iterable<? extends ManagedShutdownable> targets) {
        targets.forEach(this::register);
    }

    public void register(Iterable<? extends ManagedShutdownable> targets, int timeoutSeconds) {
        targets.forEach(target -> register(target, timeoutSeconds));
    }

    public void register(ManagedShutdownable target, int timeoutSeconds) {
        var name = target.getName();

        this.items.compute(name, (k, existing) -> {
            if (existing != null) {
                throw new BadStateException("%s already registered", name);
            }
            return new ShutdownItem(this, target, timeoutSeconds);
        });

        this.logger.info("registered shutdown: %s", name);

        refreshTimeout();
    }

    public void unregister(ManagedShutdownable target) {
        var name = target.getName();

        this.items.compute(name, (k, existing) -> {
            if (existing == null) {
                throw new BadStateException("%s not yet registered", name);
            }
            return null;
        });

        this.logger.info("unregistered shutdown: %s", name);

        refreshTimeout();
    }

    public void refreshTimeout() {
        int timeout = 0;
        for (var i : this.items.values()) {
            timeout = Math.max(timeout, i.getTimeoutSeconds());
        }
        ;
        this.maxTimeoutSeconds = timeout;
    }

    public void unhook() {
        if (this.thread == null) {
            throw new BadStateException("not ever enabled");
        }
        if (!Runtime.getRuntime().removeShutdownHook(this.thread)) {
            throw new BadStateException("failed to remove shutdown hook");
        }

        this.logger.info("shutdown hook is removed");
    }

    public void hook() {
        if (this.thread != null) {
            throw new BadStateException("already enabled");
        }

        this.thread = new Thread(() -> {
            this.logger.setInShutdownHook(true);

            try {
                shutdownAll();
            } catch (InterruptedException e) {
                this.logger.error(e, "shutdown is interrupted");
                e.printStackTrace();
            }
        });

        Runtime.getRuntime().addShutdownHook(this.thread);
        this.logger.info("shutdown hook is added");
    }

    public void shutdown(ManagedShutdownable target, int timeoutSeconds) {
        String name = target.getName();
        if (target.isShutdownNeeded() == false) {
            throw new BadStateException("%s no need shutdown", name);
        }

        var item = this.items.get(name);
        if (item == null) {
            throw new BadStateException("%s is not ever registered", name);
        }
        if (item.isShutdowned()) {
            this.items.remove(name);
            throw new BadStateException("%s is already shutdowned", name);
        }

        var th = new Thread(() -> item.shutdown(timeoutSeconds));
        th.start();

        var itemLogger = item.getLogger();
        itemLogger.info("waiting item to shutdown. timeout seconds=%is", timeoutSeconds);
        try {
            th.join(item.getTimeoutSeconds() * 1000);
        } catch (InterruptedException e) {
            itemLogger.error(e, "error occurred when joining the interrupted");
        }

        if (item.isShutdowned() == false) {
            itemLogger.info("shutdown timeout, interrupt it now");
            th.interrupt();

            try {
                th.join(100);
            } catch (InterruptedException e) {
                itemLogger.error(e, "error occurred when joining the interrupted");
            }
        }

        this.items.remove(name);
    }

    public void shutdownAll() throws InterruptedException {
        this.logger.info("Shutdown begin");

        var currItems = new ArrayList<>(this.items.values());
        currItems.removeIf(item -> !item.isShutdownNeeded());

        var latch = new CountDownLatch(currItems.size());

        var threads = new ArrayList<Thread>(currItems.size());
        currItems.forEach(item -> {
            threads.add(new Thread(() -> {
                item.shutdown(0);
                latch.countDown();
            }));
        });

        threads.forEach(Thread::start);

        this.logger.info("waiting %i item to shutdown. timeout seconds=%is", currItems.size(), this.maxTimeoutSeconds);
        latch.await(this.maxTimeoutSeconds, TimeUnit.SECONDS);
        this.logger.info("wait timeout %i", currItems.size());

        for (int i = 0; i < currItems.size(); i++) {
            var item = currItems.get(i);
            if (item.isShutdowned() == false) {
                item.getLogger().info("shutdown timeout, interrupt it now");
                threads.get(i).interrupt();
            }
        }

        for (int i = 0; i < currItems.size(); i++) {
            var item = currItems.get(i);
            if (item.isShutdowned() == false) {
                var iLogger = item.getLogger();
                iLogger.info("waiting for being interrupted for shutdown");
                try {
                    threads.get(i).join(100);
                } catch (InterruptedException e) {
                    iLogger.error(e, "error occurred when joining the interrupted");
                }
            }
        }

        var newItems = new LinkedHashMap<String, ShutdownItem>();
        currItems.forEach(item -> {
            if (item.isShutdownNeeded()) {
                newItems.put(item.getTarget().getName(), item);
            }
        });
        this.items = newItems;

        this.logger.info("Shutdown finished");
    }
}
