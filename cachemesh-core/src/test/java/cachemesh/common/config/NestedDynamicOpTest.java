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

public class NestedDynamicOpTest {

	public static class Base implements SomeConfig {
		private String kind;

		public void setKind(String kind) {
			this.kind = kind;
		}

		public String getKind() {
			return this.kind;
		}

		@Override
		public Collection<Property<?>> properties() {
			return SomeConfig.buildProperties(
						Property.builder()
							.config(Base.class).name("kind").op(StringOp.DEFAULT)
							.build());
		}
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
			var base = super.properties();
			return SomeConfig.buildProperties(
						base,
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
			var base = super.properties();
			return SomeConfig.buildProperties(
						base,
						Property.builder()
							.config(Sample2.class).name("ok").op(BooleanOp.DEFAULT)
							.build());
		}
	}

	public static class SampleOp extends NestedDynamicOp<Base> {

		public SampleOp() {
			super(Base.class,
				Map.of("1", new NestedStaticOp<>(Sample1.class),
				"2", new NestedStaticOp<>(Sample2.class)));
		}

		@Override public Object extractKey(String hint, Map<String, Object> map) {
			return map.get("kind");
		}

	}

	@Test
	public void test_happy() {
		var t = new SampleOp();

		var map1 = Map.of("kind", "1", "num", 678);
		var sample1 = t.convert("", null, map1);
		assertInstanceOf(Sample1.class, sample1);
		assertEquals(678, ((Sample1)sample1).getNum());

		var map2 = Map.of("kind", "2", "ok", true);
		var sample2 = t.convert("", null, map2);
		assertInstanceOf(Sample2.class, sample2);
		assertTrue(((Sample2)sample2).isOk());
	}

}
