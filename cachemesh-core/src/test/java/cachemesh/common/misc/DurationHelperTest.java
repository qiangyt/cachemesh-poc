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

import org.junit.jupiter.api.Test;

public class DurationHelperTest {

	@Test
	public void test_parse() {
		assertNull(DurationHelper.parse(null));
		assertNull(DurationHelper.parse(" "));

		assertEquals(1, DurationHelper.parse("1s").getSeconds());
		assertEquals(2 * 60, DurationHelper.parse("2m").getSeconds());
		assertEquals(3 * 3600, DurationHelper.parse("3h").getSeconds());
		assertEquals(4 * 86400, DurationHelper.parse("4d").getSeconds());
		assertEquals(5 * 7 * 86400, DurationHelper.parse("5w").getSeconds());

		assertEquals(1 * 7 * 86400 + 2 * 86400 + 3 * 3600 + 4 * 60 + 5,
			DurationHelper.parse("1w2d3h4m5s").getSeconds());
	}

}
