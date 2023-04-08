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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

public class EnumOpTest {

	enum Sample {
		a, b, c
	}

	@Test
	public void test_happy() {
		var t = new EnumOp<Sample>(Sample.class);

		assertSame(Sample.a, t.convert("", null, "a"));
		assertSame(Sample.b, t.convert("", null, "b"));
		assertSame(Sample.c, t.convert("", null, Sample.c));

		assertThrows(IllegalArgumentException.class, () -> t.convert("", null, new Object()));
	}

}
