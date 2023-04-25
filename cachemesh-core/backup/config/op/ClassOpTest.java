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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class ClassOpTest {

	class Sample {
	}

	@Test
	public void test_happy() {
		var t = ClassOp.DEFAULT;

		assertSame(Sample.class, t.populate("", null, Sample.class));
		assertSame(Sample.class, t.populate("", null, Sample.class.getName()));

		assertThrows(IllegalArgumentException.class, () -> t.populate("", null, new Object()));
	}

}
