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
package cachemesh.common;

import org.slf4j.Logger;

import cachemesh.common.util.LogHelper;
import lombok.Getter;

@Getter
public class LifeStage {

    public static enum Type {
        created, starting, started, stopping, stopped
    }

    private volatile Type type = Type.created;

    private final String hintKey;

    private final String hintValue;

    private final Logger logger;

    public LifeStage(String hintKey, String hintValue) {
        this(hintKey, hintValue, LogHelper.getLogger(hintKey, hintValue));
    }

    public LifeStage(String hintKey, String hintValue, Logger logger) {
        this.hintKey = hintKey;
        this.hintValue = hintValue;
        this.logger = logger;
    }

    public void expect(Type expectedType) {
        var t = getType();
        if (t != expectedType) {
            var msg = String.format("%s %s: expected be %s, but be %s", getHintKey(), getHintValue(), expectedType, t);
            throw new IllegalStateException(msg);
        }
    }

    public void expectNot(Type... expectedTypes) {
        var t = getType();
        for (var expectedType : expectedTypes) {
            if (t == expectedType) {
                var msg = String.format("%s %s: expected not %s", getHintKey(), getHintValue(), expectedType);
                throw new IllegalStateException(msg);
            }
        }
    }

    public void starting() {
        step(Type.created, Type.starting);
    }

    public void started() {
        step(Type.starting, Type.started);
    }

    public void stopping() {
        step(Type.started, Type.stopping);
    }

    public void stopped() {
        step(Type.stopping, Type.stopped);
    }

    public void step(Type current, Type next) {
        expect(current);
        getLogger().info(next.name());
        this.type = next;
    }

}
