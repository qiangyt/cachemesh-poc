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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Test;

import cachemesh.common.misc.SimpleURL;

public class SimpleUrlOpTest {

	@Test
	public void test_happy() throws MalformedURLException {
		var t = SimpleUrlOp.DEFAULT;

		assertEquals(new SimpleURL("ftp://example1.com"),
					t.convert("", null, "ftp://example1.com"));

		SimpleURL u2 = new SimpleURL("ftp://example2.com");
		assertSame(u2, t.convert("", null, u2));

		assertEquals(new SimpleURL("ftp://example3.com"),
					t.convert("", null, new URL("ftp://example3.com")));

		assertThrows(IllegalArgumentException.class, () -> t.convert("", null, new Object()));
	}

}
