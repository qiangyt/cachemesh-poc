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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Map;

import org.junit.jupiter.api.Test;

import cachemesh.common.config.Bean;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Prop;

public class DynamicOpTest {

	public static class Base implements Bean {
		private String kind;

		public void setKind(String kind) {
			this.kind = kind;
		}

		public String getKind() {
			return this.kind;
		}

		@Override
		public Iterable<Prop<?>> props() {
			return ConfigHelper.props(
						Prop.builder()
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
		public Iterable<Prop<?>> props() {
			return ConfigHelper.props(
						super.props(),
						Prop.builder()
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
						super.props(),
						Prop.builder()
							.config(Sample2.class).name("ok").op(BooleanOp.DEFAULT)
							.build());
		}
	}

	public static class SampleOp extends DynamicOp<Base> {

		public SampleOp() {
			super(Base.class,
				Map.of("1", new ReflectBeanOp<>(Sample1.class),
				"2", new ReflectBeanOp<>(Sample2.class)));
		}

		@Override public Object extractKey(String hint, Map<String, Object> parent, Map<String, Object> value) {
			return value.get("kind");
		}

	}

	@Test
	public void test_happy() {
		var t = new SampleOp();

		var map1 = Map.of("kind", "1", "num", 678);
		var sample1 = t.build("", null, map1);
		assertInstanceOf(Sample1.class, sample1);
		assertEquals(678, ((Sample1)sample1).getNum());

		var map2 = Map.of("kind", "2", "ok", true);
		var sample2 = t.build("", null, map2);
		assertInstanceOf(Sample2.class, sample2);
		assertTrue(((Sample2)sample2).isOk());
	}

}
