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
package cachemesh.common.misc.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import cachemesh.common.err.BadValueException;
import cachemesh.common.registry.Manager;

public class ManagerTest {


	class TargetManager extends Manager<String, String> {
		@Override
		public String getValueName() {
			return "target";
		}

		String kindToDestroy;
		String itemToDestroy;
		int timeoutToDestroy;
		int counterForDestroy;

		String kindToCreate;
		int counterForCreate;

		@Override
		protected String doCreate(String kind) {
			this.kindToCreate = kind;
			this.counterForCreate++;
			return "value-for-" + kind;
		}

		@Override
		protected void doDestroy(String kind, String item, int timeoutSeconds) throws InterruptedException {
			this.kindToDestroy = kind;
			this.counterForDestroy++;
			this.itemToDestroy = item;
			this.timeoutToDestroy = timeoutSeconds;
		}
	}

	@Test
	public void test_resolve() {
		var r = new TargetManager();
		var c = "c";

		var v1 = r.resolve(c);
		assertEquals("value-for-c", v1);
		assertEquals(1, r.counterForCreate);
		assertSame(c, r.kindToCreate);

		var v2 = r.resolve(c);
		assertSame(v1, v2);
		assertEquals(1, r.counterForCreate);
		assertSame(c, r.kindToCreate);

		v2 = r.resolve("c2");
		assertEquals("value-for-c2", v2);
		assertEquals(2, r.counterForCreate);
		assertEquals("c2", r.kindToCreate);
	}

	@Test
	public void test_create() {
		var r = new TargetManager();
		var c = "c";

		var v1 = r.create(c);
		assertEquals("value-for-c", v1);
		assertEquals(1, r.counterForCreate);
		assertSame(c, r.kindToCreate);

		assertThrows(BadValueException.class, () -> r.create(c));

		var v2 = r.create("c2");
		assertEquals("value-for-c2", v2);
		assertEquals(2, r.counterForCreate);
		assertEquals("c2", r.kindToCreate);
	}

	@Test
	public void test_destroy() throws Exception {
		var r = new TargetManager();
		var c = "c";

		assertNull(r.destroy(c, 12345));
		assertEquals(0, r.counterForDestroy);
		assertNull(r.kindToDestroy);

		r.create(c);

		assertEquals("value-for-c", r.destroy(c, 12345));
		assertEquals(1, r.counterForDestroy);
		assertSame(c, r.kindToDestroy);

	}

}
