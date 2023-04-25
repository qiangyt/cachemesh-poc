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

	
	class Bean {
		@Getter	@Setter
		String firstName;

		String lastName;

		@Getter	@Setter
		boolean local;

		public String retrieveLastName() {
			return this.lastName;
		}

		public void populateLastName(String lastName) {
			this.lastName = lastName;
		}
	}

	@Test
	public void test_getter() {
		var firstName = Reflect.getter(Bean.class, "firstName", String.class, null);
		assertEquals("getFirstName", firstName.getName());
		
		var local = Reflect.getter(Bean.class, "local", boolean.class, null);
		assertEquals("isLocal", local.getName());
		
		var lastName = Reflect.getter(Bean.class, "firstName", String.class, "retrieveLastName");
		assertEquals("retrieveLastName", lastName.getName());
	}

	@Test
	public void test_setter() {
	var firstName = Reflect.setter(Bean.class, "firstName", String.class, null);
	assertEquals("setFirstName", firstName.getName());
	
	var local = Reflect.setter(Bean.class, "local", boolean.class, null);
	assertEquals("setLocal", local.getName());
	
	var lastName = Reflect.setter(Bean.class, "firstName", String.class, "populateLastName");
	assertEquals("populateLastName", lastName.getName());
	}

}
