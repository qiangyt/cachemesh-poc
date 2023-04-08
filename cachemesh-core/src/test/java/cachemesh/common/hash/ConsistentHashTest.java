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
package cachemesh.common.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ConsistentHashTest {

	class Node implements HasKey {

		final String key;

		Node(String key) {
			this.key = key;
		}

		@Override public String getKey() {
			return this.key;
		}

		@Override public String toString() {
			return this.key;
		}
	}

	ConsistentHash<Node> newConsistentHashTest() {
		return new ConsistentHash<>(MurmurHash.DEFAULT);
	}

	@Test
	public void test_empty() {
		var c = newConsistentHashTest();
		assertSame(MurmurHash.DEFAULT, c.algo());

		assertEquals(0, c.nodeSize());
		assertEquals(0, c.virtualNodeSize());

		assertFalse(c.nodes().iterator().hasNext());
		assertNull(c.findNode("k"));
		assertNull(c.virtualNodeFor("k"));
		assertNull(c.virtualNodeFor(c.hash("k")));
	}

	@Test
	public void test_1() {
		var c = newConsistentHashTest();

		var n = new Node("k");
		c.join(n);

		assertEquals(1, c.nodeSize());
		assertEquals(160, c.virtualNodeSize());
		assertSame(n, c.findNode("k"));

		assertSame(n, c.findNode("k1"));
	}

	@Test
	public void test_2() {
		var c = newConsistentHashTest();

		var n1 = new Node("n1");
		c.join(n1);

		var n2 = new Node("n2");
		c.join(n2);

		assertEquals(2, c.nodeSize());
		assertEquals(160 * 2, c.virtualNodeSize());

		int onNode1 = 0, onNode2 = 0;
		for (var i = 0; i < 10000; i++) {
			var n = c.findNode("" + i);
			assertTrue(n == n1 || n == n2);

			if (n == n1) {
				onNode1++;
			} else if (n == n2) {
				onNode2++;
			} else {

			}
		}

		assertTrue(onNode1 > 10000 / 3);
		assertTrue(onNode2 > 10000 / 3);
	}

}
