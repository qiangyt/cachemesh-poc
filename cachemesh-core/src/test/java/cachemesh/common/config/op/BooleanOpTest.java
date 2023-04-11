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
package cachemesh.common.config.op;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class BooleanOpTest {

	@Test
	public void test_happy() {
		var t = BooleanOp.DEFAULT;


		assertTrue(t.populate("", null, Boolean.TRUE));
		assertTrue(t.populate("", null, true));
		assertFalse(t.populate("", null, Boolean.FALSE));
		assertFalse(t.populate("", null, false));

		assertTrue(t.populate("", null, "True"));
		assertTrue(t.populate("", null, "TRUE"));
		assertTrue(t.populate("", null, "true"));
		assertTrue(t.populate("", null, "T"));
		assertTrue(t.populate("", null, "t"));
		assertFalse(t.populate("", null, "False"));
		assertFalse(t.populate("", null, "FALSE"));
		assertFalse(t.populate("", null, "false"));
		assertTrue(t.populate("", null, "Yes"));
		assertTrue(t.populate("", null, "YES"));
		assertTrue(t.populate("", null, "yes"));
		assertTrue(t.populate("", null, "Y"));
		assertTrue(t.populate("", null, "y"));
		assertFalse(t.populate("", null, "No"));
		assertFalse(t.populate("", null, "NO"));
		assertFalse(t.populate("", null, "no"));
		assertTrue(t.populate("", null, "Ok"));
		assertTrue(t.populate("", null, "ok"));
		assertTrue(t.populate("", null, "OK"));
		assertTrue(t.populate("", null, "Okay"));
		assertTrue(t.populate("", null, "OKAY"));
		assertTrue(t.populate("", null, "okay"));

		assertTrue(t.populate("", null, 'T'));
		assertTrue(t.populate("", null, 't'));
		assertFalse(t.populate("", null, 'F'));
		assertFalse(t.populate("", null, 'f'));
		assertTrue(t.populate("", null, 'Y'));
		assertTrue(t.populate("", null, 'y'));
		assertFalse(t.populate("", null, 'N'));
		assertFalse(t.populate("", null, 'n'));

		for (int i = -10; i < 0; i++) {
			assertTrue(t.populate("", null, i));
		}
		assertFalse(t.populate("", null, 0));
		for (int i = 1; i <= 10; i++) {
			assertTrue(t.populate("", null, i));
		}
	}

	@Test
	public void test_failure() {
		var t = BooleanOp.DEFAULT;

		assertThrows(IllegalArgumentException.class, () -> t.populate("", null, new Object()));
	}

}
