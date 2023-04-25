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
package cachemesh.common.config3.types;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class BooleanTypeTest {

	@Test
	public void test_happy() {
		var t = BooleanType.DEFAULT;

		assertTrue(t.convert(null, Boolean.TRUE));
		assertTrue(t.convert(null, true));
		assertFalse(t.convert(null, Boolean.FALSE));
		assertFalse(t.convert(null, false));

		assertTrue(t.convert(null, "True"));
		assertTrue(t.convert(null, "TRUE"));
		assertTrue(t.convert(null, "true"));
		assertTrue(t.convert(null, "T"));
		assertTrue(t.convert(null, "t"));
		assertFalse(t.convert(null, "False"));
		assertFalse(t.convert(null, "FALSE"));
		assertFalse(t.convert(null, "false"));
		assertTrue(t.convert(null, "Yes"));
		assertTrue(t.convert(null, "YES"));
		assertTrue(t.convert(null, "yes"));
		assertTrue(t.convert(null, "Y"));
		assertTrue(t.convert(null, "y"));
		assertFalse(t.convert(null, "No"));
		assertFalse(t.convert(null, "NO"));
		assertFalse(t.convert(null, "no"));
		assertTrue(t.convert(null, "Ok"));
		assertTrue(t.convert(null, "ok"));
		assertTrue(t.convert(null, "OK"));
		assertTrue(t.convert(null, "Okay"));
		assertTrue(t.convert(null, "OKAY"));
		assertTrue(t.convert(null, "okay"));

		assertTrue(t.convert(null, 'T'));
		assertTrue(t.convert(null, 't'));
		assertFalse(t.convert(null, 'F'));
		assertFalse(t.convert(null, 'f'));
		assertTrue(t.convert(null, 'Y'));
		assertTrue(t.convert(null, 'y'));
		assertFalse(t.convert(null, 'N'));
		assertFalse(t.convert(null, 'n'));

		for (int i = -10; i < 0; i++) {
			assertTrue(t.convert(null, i));
		}
		assertFalse(t.convert(null, 0));
		for (int i = 1; i <= 10; i++) {
			assertTrue(t.convert(null, i));
		}
	}

	@Test
	public void test_failure() {
		var t = BooleanType.DEFAULT;

		assertThrows(IllegalArgumentException.class, () -> t.convert(null, new Object()));
	}

}
