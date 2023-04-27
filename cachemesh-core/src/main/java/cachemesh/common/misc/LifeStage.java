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
package cachemesh.common.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.err.BadStateException;
import lombok.Getter;

@Getter
public class LifeStage {

    public static enum Stage {
        created, starting, started, stopping, stopped
    }

    private volatile Stage stage = Stage.created;

    private final String hintKey;

    private final String hintValue;

    private final Logger logger;

    public LifeStage(String hintKey, String hintValue) {
        this(hintKey, hintValue, LoggerFactory.getLogger(hintValue + "@" + hintKey));
    }

    public LifeStage(String hintKey, String hintValue, Logger logger) {
        this.hintKey = hintKey;
        this.hintValue = hintValue;
        this.logger = logger;
    }

    public void expect(Stage expectedType) {
        var t = getStage();
        if (t != expectedType) {
            throw new BadStateException("%s %s: expected be %s, but be %s", getHintKey(), getHintValue(), expectedType,
                    t);
        }
    }

    public void expectNot(Stage... expectedTypes) {
        var t = getStage();
        for (var expectedType : expectedTypes) {
            if (t == expectedType) {
                throw new BadStateException("%s %s: expected not %s", getHintKey(), getHintValue(), expectedType);
            }
        }
    }

    public void starting() {
        step(Stage.created, Stage.starting);
    }

    public void started() {
        step(Stage.starting, Stage.started);
    }

    public void stopping() {
        step(Stage.started, Stage.stopping);
    }

    public void stopped() {
        step(Stage.stopping, Stage.stopped);
    }

    public void step(Stage current, Stage next) {
        expect(current);
        getLogger().info(next.name());
        this.stage = next;
    }

}
