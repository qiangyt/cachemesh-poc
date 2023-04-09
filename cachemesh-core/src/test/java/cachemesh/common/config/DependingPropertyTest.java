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

import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.Test;


public class DependingPropertyTest {

	public static abstract class Base implements SomeConfig {

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
		public Collection<Property<?>> properties() {
			return PropertyHelper.buildProperties(
						Property.builder()
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
		public Collection<Property<?>> properties() {
			return PropertyHelper.buildProperties(
						Property.builder()
							.config(Sample2.class).name("ok").op(BooleanOp.DEFAULT)
							.build());
		}
	}

	public static class TestConfig implements SomeConfig {
		private String kind;

		private Base target;

		public void setKind(String kind) {
			this.kind = kind;
		}

		public String getKind() {
			return this.kind;
		}

		public void setTarget(Base target) {
			this.target = target;
		}

		public Base getTarget() {
			return this.target;
		}

		@Override
		public Collection<Property<?>> properties() {
			var kindProp = new Property<>(TestConfig.class, "kind", null, StringOp.DEFAULT);
			var dispatchOpMap = Map.of("1", new NestedStaticOp<>(Sample1.class),
									"2", new NestedStaticOp<>(Sample2.class));
			var targetProp = new DependingProperty<Base, String>(TestConfig.class, Base.class, "target", kindProp, dispatchOpMap);
			return PropertyHelper.buildProperties(kindProp, targetProp);
		}
	}

	@Test
	public void test_happy() {
		var t = new NestedStaticOp<TestConfig>(TestConfig.class) {
			@Override
			public TestConfig newValue(String hint, Map<String, Object> parentObject, Map<String, Object> map) {
				return new TestConfig();
			}
		};

		var map1 = Map.of("kind", "1", "target", Map.of("num", 678));
		var c1 = t.convert("", null, map1);
		assertEquals("1", c1.getKind());

		var sample1 = c1.getTarget();
		assertInstanceOf(Sample1.class, sample1);
		assertEquals(678, ((Sample1)sample1).getNum());

		var map2 = Map.of("kind", "2", "target", Map.of("ok", true));
		var c2 = t.convert("", null, map2);
		assertEquals("2", c2.getKind());

		var sample2 = c2.getTarget();
		assertInstanceOf(Sample2.class, sample2);
		assertTrue(((Sample2)sample2).isOk());
	}

}
