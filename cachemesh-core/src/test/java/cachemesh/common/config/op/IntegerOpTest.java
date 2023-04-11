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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class IntegerOpTest {

	@Test
	public void test_happy() {
		var t = IntegerOp.DEFAULT;

		assertEquals(1, t.populate("", null, "1"));
		assertEquals('2', t.populate("", null, '2'));
		assertEquals(3, t.populate("", null, Byte.valueOf("3")));
		assertEquals(4, t.populate("", null, Short.valueOf("4")));
		assertEquals(5, t.populate("", null, Integer.valueOf(5)));
		assertEquals(6, t.populate("", null, Long.valueOf(6)));
		assertEquals(7, t.populate("", null, Float.valueOf(7)));
		assertEquals(8, t.populate("", null, Double.valueOf(8)));

		assertThrows(IllegalArgumentException.class, () -> t.populate("", null, new Object()));
	}

}
