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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.URL;

import org.junit.jupiter.api.Test;


public class SimpleURLTest {

	@Test
	public void test_happy() throws Exception {
		String text1 = "http://user:pwd@example.com:7086/base?k=v";

		var t1 = new SimpleURL(text1);

		assertEquals("/base", t1.getPath());
		assertEquals("user:pwd@example.com:7086", t1.getAuthority());
		assertEquals(7086, t1.getPort());
		assertEquals("http", t1.getProtocol());
		assertEquals("example.com", t1.getHost());
		assertEquals(text1, t1.toString());

		var q = t1.getQuery();
		assertEquals(1, q.size());
		assertEquals("v", q.get("k"));

		var t2 = new SimpleURL("http://user:pwd@example.com:7086/base#ref");
		assertEquals("ref", t2.getRef());
		assertNotEquals(t1.hashCode(), t2.hashCode());
		assertFalse(t2.equals(t1));
		assertTrue(t2.equals(new URL("http://user:pwd@example.com:7086/base#ref")));
	}

}
