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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cachemesh.common.misc.LifeStage.Stage;

public class LifeStageTest {

	@Test
	public void test_happy() {
		var t = new LifeStage("LifeStageTest", "test_happy");
		assertEquals("test_happy@LifeStageTest", t.getLogger().getName());

		t.starting();
		t.expect(Stage.starting);
		assertEquals(Stage.starting, t.getStage());

		t.started();
		t.expect(Stage.started);
		assertEquals(Stage.started, t.getStage());

		t.stopping();
		t.expect(Stage.stopping);
		assertEquals(Stage.stopping, t.getStage());

		t.stopped();
		t.expect(Stage.stopped);
		assertEquals(Stage.stopped, t.getStage());

		t.expectNot(Stage.starting, Stage.started, Stage.stopping);
	}

}
