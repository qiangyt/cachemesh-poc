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

import java.util.Map;

import org.junit.jupiter.api.Test;

import cachemesh.common.config.Bean;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Prop;

public class ReflectBeanOpTest {

	public static class Sample implements Bean {
		private Integer num;

		public Sample() {}

		public void setNum(Integer num) {
			this.num = num;
		}

		public Integer getNum() {
			return this.num;
		}

		@Override
		public Iterable<Prop<?>> props() {
			return ConfigHelper.props(
						Prop.builder()
							.config(Sample.class).name("num").op(IntegerOp.DEFAULT)
							.build());
		}
	}

	@Test
	public void test_happy() {
		var t = new ReflectBeanOp<Sample>(Sample.class);

		var sample = t.build("", null, Map.of("num", 345));
		assertEquals(345, sample.num);
	}

}
