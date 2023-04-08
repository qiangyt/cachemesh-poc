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

import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.Setter;

public class ReflectTest {

	@Getter
	@Setter
	class Bean {
		String firstName;
		String lastName;
		boolean local;
	}

	@Test
	public void test_getterName() {
		assertEquals("getTarget", Reflect.getterName("target", String.class));
		assertEquals("isOk", Reflect.getterName("ok", boolean.class));
		assertEquals("isRight", Reflect.getterName("right", Boolean.class));
	}

	@Test
	public void test_setterName() {
		assertEquals("setTarget", Reflect.setterName("target"));
	}

}
