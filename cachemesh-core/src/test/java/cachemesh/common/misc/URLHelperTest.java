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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class URLHelperTest {

	@Test
	public void test_parseQuery_happy() {
		var t = URLHelper.parseQuery("a=1&b=2&c=&d");
		assertEquals(3, t.size());
		assertEquals("1", t.get("a"));
		assertEquals("2", t.get("b"));
		assertTrue(t.get("c").isEmpty());
		assertFalse(t.containsKey("d"));
	}

	@Test
	public void test_parseQuery_fault() {
		assertTrue(URLHelper.parseQuery("").isEmpty());

		var t = URLHelper.parseQuery("a=  &&");
		assertEquals(1, t.size());
		assertTrue(t.get("a").isEmpty());
	}

}
