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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


import org.junit.jupiter.api.Test;

public class RegistryTest {

	class Config {
		final String name;

		Config(String name) {
			this.name = name;
		}
	}

	class TargetRegistry extends Registry<Config, String> {
		@Override
		protected String retrieveKey(Config config) {
			return config.name;
		}
	}

	@Test
	public void test_happy() {
		var r = new TargetRegistry();
		var c1 = new Config("c1");

		assertNull(r.unregister(c1));
		assertNull(r.get(c1));
		r.register(c1, "value1");
		assertEquals("value1", r.get(c1));
		assertEquals("value1", r.getByKey("c1"));

		var c2 = new Config("c1");
		assertThrows(IllegalArgumentException.class, () -> r.register(c2, "value2"));

		assertEquals("value1", r.unregister(c2));
		r.register(c2, "value2");
		assertEquals("value2", r.get(c2));
	}

}
