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
package cachemesh.common.config;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class BooleanOpTest {

	@Test
	public void test_happy() {
		var t = BooleanOp.DEFAULT;


		assertTrue(t.convert("", Boolean.TRUE));
		assertTrue(t.convert("", true));
		assertFalse(t.convert("", Boolean.FALSE));
		assertFalse(t.convert("", false));

		assertTrue(t.convert("", "True"));
		assertTrue(t.convert("", "TRUE"));
		assertTrue(t.convert("", "true"));
		assertTrue(t.convert("", "T"));
		assertTrue(t.convert("", "t"));
		assertFalse(t.convert("", "False"));
		assertFalse(t.convert("", "FALSE"));
		assertFalse(t.convert("", "false"));
		assertTrue(t.convert("", "Yes"));
		assertTrue(t.convert("", "YES"));
		assertTrue(t.convert("", "yes"));
		assertTrue(t.convert("", "Y"));
		assertTrue(t.convert("", "y"));
		assertFalse(t.convert("", "No"));
		assertFalse(t.convert("", "NO"));
		assertFalse(t.convert("", "no"));
		assertTrue(t.convert("", "Ok"));
		assertTrue(t.convert("", "ok"));
		assertTrue(t.convert("", "OK"));
		assertTrue(t.convert("", "Okay"));
		assertTrue(t.convert("", "OKAY"));
		assertTrue(t.convert("", "okay"));

		assertTrue(t.convert("", 'T'));
		assertTrue(t.convert("", 't'));
		assertFalse(t.convert("", 'F'));
		assertFalse(t.convert("", 'f'));
		assertTrue(t.convert("", 'Y'));
		assertTrue(t.convert("", 'y'));
		assertFalse(t.convert("", 'N'));
		assertFalse(t.convert("", 'n'));

		for (int i = -10; i < 0; i++) {
			assertTrue(t.convert("", i));
		}
		assertFalse(t.convert("", 0));
		for (int i = 1; i <= 10; i++) {
			assertTrue(t.convert("", i));
		}
	}

	@Test
	public void test_failure() {
		var t = BooleanOp.DEFAULT;

		assertThrows(IllegalArgumentException.class, () -> t.convert("", new Object()));
	}

}
