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
package cachemesh.common.config.types;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.Test;

import cachemesh.common.err.BadValueException;

public class DurationTypeTest {

	@Test
	public void test_happy() {
		var t = DurationType.DEFAULT;

		var d1 =Duration.ofDays(123);
		assertSame(d1, t.convert(null, d1));

		var d2 =Duration.ofSeconds(123);
		assertEquals(d2, t.convert(null, "123s"));

		assertThrows(BadValueException.class, () -> t.convert(null, new Object()));
	}

}
