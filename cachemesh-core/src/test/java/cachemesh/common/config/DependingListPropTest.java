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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import cachemesh.common.config.op.BooleanOp;
import cachemesh.common.config.op.IntegerOp;
import cachemesh.common.config.op.ReflectOp;
import cachemesh.common.config.op.StringOp;


public class DependingListPropTest {

	public static abstract class Base implements Bean {

	}

	public static class Sample1 extends Base {
		private Integer num;

		public Sample1() {}

		public void setNum(Integer num) {
			this.num = num;
		}

		public Integer getNum() {
			return this.num;
		}

		@Override
		public Iterable<Prop<?>> props() {
			return ConfigHelper.props(
						ReflectProp.builder()
							.config(Sample1.class).name("num").op(IntegerOp.DEFAULT)
							.build());
		}
	}

	public static class Sample2 extends Base {
		private Boolean ok;

		public Sample2() {}

		public void setOk(Boolean ok) {
			this.ok = ok;
		}

		public Boolean isOk() {
			return this.ok;
		}

		@Override
		public Iterable<Prop<?>> props() {
			return ConfigHelper.props(
						ReflectProp.builder()
							.config(Sample2.class).name("ok").op(BooleanOp.DEFAULT)
							.build());
		}
	}

	public static class TestConfig implements Bean {
		private String kind;

		private List<Base> target;

		public void setKind(String kind) {
			this.kind = kind;
		}

		public String getKind() {
			return this.kind;
		}

		public void setTarget(List<Base> target) {
			this.target = target;
		}

		public List<Base> getTarget() {
			return this.target;
		}

		@Override
		public Iterable<Prop<?>> props() {
			var kindProp = new ReflectProp<>(TestConfig.class, "kind", null, StringOp.DEFAULT);
			var dispatchOpMap = Map.of("1", new ReflectOp<>(Sample1.class),
									"2", new ReflectOp<>(Sample2.class));
			var targetProp = new DependingListProp<Base, String>(TestConfig.class, Base.class, "target", kindProp, dispatchOpMap);
			return ConfigHelper.props(kindProp, targetProp);
		}
	}

	@Test
	public void test_happy() {
		var t = new ReflectOp<TestConfig>(TestConfig.class) {
			@Override
			public TestConfig newBean(String hint, Map<String, Object> parent, Map<String, Object> value) {
				return new TestConfig();
			}
		};

		var map1 = Map.of("kind", "1", "target", List.of(Map.of("num", 678), Map.of("num", 543)));
		var c1 = t.populate("", null, map1);
		assertEquals("1", c1.getKind());


		assertEquals(2, c1.getTarget().size());

		var sample11 = c1.getTarget().get(0);
		assertInstanceOf(Sample1.class, sample11);
		assertEquals(678, ((Sample1)sample11).getNum());

		var sample12 = c1.getTarget().get(1);
		assertInstanceOf(Sample1.class, sample12);
		assertEquals(543, ((Sample1)sample12).getNum());

		var map2 = Map.of("kind", "2", "target", List.of(Map.of("ok", true)));
		var c2 = t.populate("", null, map2);
		assertEquals("2", c2.getKind());

		var sample2 = c2.getTarget().get(0);
		assertInstanceOf(Sample2.class, sample2);
		assertTrue(((Sample2)sample2).isOk());
	}

}
