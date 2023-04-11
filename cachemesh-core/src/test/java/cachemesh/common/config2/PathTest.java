package cachemesh.common.config2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

	@Test
	public void test_ROOT() {
		assertTrue(Path.ROOT.isRoot());
		assertFalse(Path.ROOT.isKeep());
		assertFalse(Path.ROOT.isBackward());
		assertEquals("/", Path.ROOT.name());
		assertEquals("/", Path.ROOT.toString());
		assertThrows(IllegalArgumentException.class, Path.ROOT::parent);

		assertSame(Path.ROOT, Path.of("/"));
		assertSame(Path.ROOT, Path.of("//"));

		assertSame(Path.ROOT, Path.of("/."));
		assertSame(Path.ROOT, Path.of("/./"));
		assertSame(Path.ROOT, Path.of("//."));
	}

	@Test
	public void test_KEEP() {
		assertFalse(Path.KEEP.isRoot());
		assertTrue(Path.KEEP.isKeep());
		assertFalse(Path.KEEP.isBackward());

		assertEquals(".", Path.KEEP.name());
		assertEquals(".", Path.KEEP.toString());
		assertNull(Path.KEEP.parent());

		assertSame(Path.KEEP, Path.of("."));
		assertSame(Path.KEEP, Path.of("./"));
		assertSame(Path.KEEP, Path.of("./."));
	}

	void assert_keep_1(Path p1) {
		assertFalse(p1.isRoot());
		assertFalse(p1.isKeep());
		assertFalse(p1.isBackward());

		assertEquals("1", p1.name());

		assertEquals(Path.KEEP, p1.parent());
		assertSame(Path.KEEP, p1.parent());

		assertEquals("./1", p1.toString());

		assertEquals(Path.of("1"), p1);
		assertEquals(Path.of("./1"), p1);
		assertNotEquals(Path.of("2"), p1);

		assertEquals(Path.of("1").hashCode(), p1.hashCode());
		assertEquals(Path.of("./1").hashCode(), p1.hashCode());
		assertNotEquals(Path.of("2").hashCode(), p1.hashCode());
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
		assertEquals("2", p2.name());
		assertEquals("./1/2", p2.toString());

		assertEquals(Path.of("1/2"), p2);
		assertEquals(Path.of("./1/2"), p2);

		var p1 = p2.parent();
		assert_keep_1(p1);
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
		assertEquals("1", p1.name());

		assertEquals(Path.ROOT, p1.parent());
		assertSame(Path.ROOT, p1.parent());

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
	}

	void assert_absolute_2(Path p2) {
		assertEquals("2", p2.name());
		assertEquals("/1/2", p2.toString());

		var p1 = p2.parent();
		assert_absolute_1(p1);
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

	void assert_backward_1(Path p1) {
		assertFalse(p1.isRoot());
		assertFalse(p1.isKeep());
		assertTrue(p1.isBackward());

		assertEquals("..", p1.name());
		assertSame(Path.KEEP, p1.parent());
		assertEquals("./..", p1.toString());
	}

	@Test
	public void test_backward_1() {
		var p1 = Path.of("..");
		assert_backward_1(p1);

		p1 = Path.of("../");
		assert_backward_1(p1);

		p1 = Path.of("./../");
		assert_backward_1(p1);

		p1 = Path.of("../.");
		assert_backward_1(p1);
	}

	void assert_backward_2(Path p2) {
		assertEquals("..", p2.name());
		assertEquals("./../..", p2.toString());

		var p1 = p2.parent();
		assert_backward_1(p1);
	}

	@Test
	public void test_backward_2() {
		var p2 = Path.of("../..");
		assert_backward_2(p2);
	}

}
