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


		assertTrue(t.build("", null, Boolean.TRUE));
		assertTrue(t.build("", null, true));
		assertFalse(t.build("", null, Boolean.FALSE));
		assertFalse(t.build("", null, false));

		assertTrue(t.build("", null, "True"));
		assertTrue(t.build("", null, "TRUE"));
		assertTrue(t.build("", null, "true"));
		assertTrue(t.build("", null, "T"));
		assertTrue(t.build("", null, "t"));
		assertFalse(t.build("", null, "False"));
		assertFalse(t.build("", null, "FALSE"));
		assertFalse(t.build("", null, "false"));
		assertTrue(t.build("", null, "Yes"));
		assertTrue(t.build("", null, "YES"));
		assertTrue(t.build("", null, "yes"));
		assertTrue(t.build("", null, "Y"));
		assertTrue(t.build("", null, "y"));
		assertFalse(t.build("", null, "No"));
		assertFalse(t.build("", null, "NO"));
		assertFalse(t.build("", null, "no"));
		assertTrue(t.build("", null, "Ok"));
		assertTrue(t.build("", null, "ok"));
		assertTrue(t.build("", null, "OK"));
		assertTrue(t.build("", null, "Okay"));
		assertTrue(t.build("", null, "OKAY"));
		assertTrue(t.build("", null, "okay"));

		assertTrue(t.build("", null, 'T'));
		assertTrue(t.build("", null, 't'));
		assertFalse(t.build("", null, 'F'));
		assertFalse(t.build("", null, 'f'));
		assertTrue(t.build("", null, 'Y'));
		assertTrue(t.build("", null, 'y'));
		assertFalse(t.build("", null, 'N'));
		assertFalse(t.build("", null, 'n'));

		for (int i = -10; i < 0; i++) {
			assertTrue(t.build("", null, i));
		}
		assertFalse(t.build("", null, 0));
		for (int i = 1; i <= 10; i++) {
			assertTrue(t.build("", null, i));
		}
	}

	@Test
	public void test_failure() {
		var t = BooleanOp.DEFAULT;

		assertThrows(IllegalArgumentException.class, () -> t.build("", null, new Object()));
	}

}
