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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

	@Test
	public void test_ROOT() {
		assertTrue(Path.ROOT.isAbsolute());
		assertFalse(Path.ROOT.isKeep());
		assertFalse(Path.ROOT.isUpward());
		assertTrue(Path.ROOT.isRoot());
		assertEquals("/", Path.ROOT.getName());
		assertEquals("/", Path.ROOT.toString());
		assertNull(Path.ROOT.getParent());

		assertSame(Path.ROOT, Path.of("/"));
		assertSame(Path.ROOT, Path.of("//"));

		assertSame(Path.ROOT, Path.of("/."));
		assertSame(Path.ROOT, Path.of("/./"));
		assertSame(Path.ROOT, Path.of("//."));

		var chain = Path.ROOT.toChain();
		assertEquals(1, chain.size());
		assertSame(Path.ROOT, chain.get(0));
	}

	@Test
	public void test_KEEP() {
		assertFalse(Path.KEEP.isAbsolute());
		assertTrue(Path.KEEP.isKeep());
		assertFalse(Path.KEEP.isUpward());
		assertFalse(Path.KEEP.isRoot());

		assertEquals(".", Path.KEEP.getName());
		assertEquals(".", Path.KEEP.toString());
		assertNull(Path.KEEP.getParent());

		assertSame(Path.KEEP, Path.of("."));
		assertSame(Path.KEEP, Path.of("./"));
		assertSame(Path.KEEP, Path.of("./."));
		
		var chain = Path.KEEP.toChain();
		assertEquals(1, chain.size());
		assertSame(Path.KEEP, chain.get(0));
	}

	void assert_keep_1(Path p1) {
		assertFalse(p1.isAbsolute());
		assertFalse(p1.isKeep());
		assertFalse(p1.isUpward());
		assertFalse(p1.isRoot());

		assertEquals("1", p1.getName());

		assertEquals(Path.KEEP, p1.getParent());
		assertSame(Path.KEEP, p1.getParent());

		assertEquals("./1", p1.toString());

		assertEquals(Path.of("1"), p1);
		assertEquals(Path.of("./1"), p1);
		assertNotEquals(Path.of("2"), p1);

		assertEquals(Path.of("1").hashCode(), p1.hashCode());
		assertEquals(Path.of("./1").hashCode(), p1.hashCode());
		assertNotEquals(Path.of("2").hashCode(), p1.hashCode());

		var chain = p1.toChain();
		assertEquals(2, chain.size());
		assertSame(Path.KEEP, chain.get(0));
		assertSame(p1, chain.get(1));
	}

	@Test
	public void test_keep_1() {
		var p1 = Path.of("1");
		assert_keep_1(p1);

		p1 = Path.of("./1");
		assert_keep_1(p1);

		p1 = Path.of(".//1");
		assert_keep_1(p1);

		p1 = Path.of("././1");
		assert_keep_1(p1);

		p1 = Path.of("1/");
		assert_keep_1(p1);

		p1 = Path.of("1/.");
		assert_keep_1(p1);

		p1 = Path.of("1//.");
		assert_keep_1(p1);

		p1 = Path.of("1/./.");
		assert_keep_1(p1);

		p1 = Path.of("./1/.");
		assert_keep_1(p1);

		p1 = Path.of("././1/././");
		assert_keep_1(p1);
	}

	void assert_keep_2(Path p2) {
		assertFalse(p2.isAbsolute());		
		assertFalse(p2.isKeep());		
		assertFalse(p2.isUpward());
		assertFalse(p2.isRoot());

		assertEquals("2", p2.getName());
		assertEquals("./1/2", p2.toString());

		assertEquals(Path.of("1/2"), p2);
		assertEquals(Path.of("./1/2"), p2);

		var p1 = p2.getParent();
		assert_keep_1(p1);		

		var chain = p2.toChain();
		assertEquals(3, chain.size());
		assertSame(Path.KEEP, chain.get(0));
		assertSame(p1, chain.get(1));
		assertSame(p2, chain.get(2));
	}

	@Test
	public void test_keep_2() {
		var p2 = Path.of("1/2");
		assert_keep_2(p2);

		p2 = Path.of("./1/2");
		assert_keep_2(p2);

		p2 = Path.of("1/./2");
		assert_keep_2(p2);

		p2 = Path.of("1/2/.");
		assert_keep_2(p2);

		p2 = Path.of("././1/2");
		assert_keep_2(p2);

		p2 = Path.of("1/././2");
		assert_keep_2(p2);

		p2 = Path.of("1/2/./.");
		assert_keep_2(p2);

		p2 = Path.of("././1/2/./.");
		assert_keep_2(p2);

		p2 = Path.of("././1/./2/./.");
		assert_keep_2(p2);

		p2 = Path.of("././1/././2/./.");
		assert_keep_2(p2);
	}

	void assert_absolute_1(Path p1) {
		assertTrue(p1.isAbsolute());
		assertFalse(p1.isKeep());
		assertFalse(p1.isUpward());
		assertFalse(p1.isRoot());

		assertEquals("1", p1.getName());

		assertEquals(Path.ROOT, p1.getParent());
		assertSame(Path.ROOT, p1.getParent());

		assertEquals("/1", p1.toString());

		assertEquals(Path.of("/1"), p1);
		assertNotEquals(Path.of("1"), p1);

		assertEquals(Path.of("/1").hashCode(), p1.hashCode());
		assertNotEquals(Path.of("1").hashCode(), p1.hashCode());
	}

	@Test
	public void test_absolute_1() {
		var p1 = Path.of("/1");
		assert_absolute_1(p1);

		p1 = Path.of("/1/");
		assert_absolute_1(p1);

		p1 = Path.of("/1/.");
		assert_absolute_1(p1);

		p1 = Path.of("//1");
		assert_absolute_1(p1);

		p1 = Path.of("//1/.");
		assert_absolute_1(p1);

		p1 = Path.of("/./1");
		assert_absolute_1(p1);

		p1 = Path.of("//1//");
		assert_absolute_1(p1);

		p1 = Path.of("//1/");
		assert_absolute_1(p1);

		p1 = Path.of("/1//");
		assert_absolute_1(p1);	

		var chain = p1.toChain();
		assertEquals(2, chain.size());
		assertSame(Path.ROOT, chain.get(0));
		assertSame(p1, chain.get(1));
	}

	void assert_absolute_2(Path p2) {
		assertTrue(p2.isAbsolute());
		assertFalse(p2.isKeep());
		assertFalse(p2.isUpward());
		assertFalse(p2.isRoot());

		assertEquals("2", p2.getName());
		assertEquals("/1/2", p2.toString());

		var p1 = p2.getParent();
		assert_absolute_1(p1);			

		var chain = p2.toChain();
		assertEquals(3, chain.size());
		assertSame(Path.ROOT, chain.get(0));
		assertSame(p1, chain.get(1));
		assertSame(p2, chain.get(2));
	}

	@Test
	public void test_absolute_2() {
		var p2 = Path.of("/1/2");
		assert_absolute_2(p2);

		p2 = Path.of("/1/2/");
		assert_absolute_2(p2);

		p2 = Path.of("/1/./2");
		assert_absolute_2(p2);

		p2 = Path.of("/1/2/.");
		assert_absolute_2(p2);
	}

	void assert_upward_1(Path p1) {
		assertFalse(p1.isAbsolute());
		assertFalse(p1.isKeep());
		assertTrue(p1.isUpward());
		assertFalse(p1.isRoot());

		assertEquals("..", p1.getName());
		assertSame(Path.KEEP, p1.getParent());
		assertEquals("./..", p1.toString());		

		var chain = p1.toChain();
		assertEquals(2, chain.size());
		assertSame(Path.KEEP, chain.get(0));
		assertSame(p1, chain.get(1));
	}

	@Test
	public void test_upward_1() {
		var p1 = Path.of("..");
		assert_upward_1(p1);

		p1 = Path.of("../");
		assert_upward_1(p1);

		p1 = Path.of("./../");
		assert_upward_1(p1);

		p1 = Path.of("../.");
		assert_upward_1(p1);
	}

	void assert_upward_2(Path p2) {
		assertFalse(p2.isAbsolute());
		assertFalse(p2.isKeep());
		assertTrue(p2.isUpward());
		assertFalse(p2.isRoot());

		assertEquals("..", p2.getName());
		assertEquals("./../..", p2.toString());

		var p1 = p2.getParent();
		assert_upward_1(p1);	

		var chain = p2.toChain();
		assertEquals(3, chain.size());
		assertSame(Path.KEEP, chain.get(0));
		assertSame(p1, chain.get(1));
		assertSame(p2, chain.get(2));
	}

	@Test
	public void test_upward_2() {
		var p2 = Path.of("../..");
		assert_upward_2(p2);
	}

}
