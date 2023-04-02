/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cachemesh.core.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import cachemesh.common.HasName;
import cachemesh.common.shutdown.Shutdownable;

public interface LocalCache	extends Shutdownable, HasName {

	@Override
	default Map<String, Object> toMap() {
		Map<String, Object> r = new HashMap<>();
		r.put("config", getConfig().toMap());
		return r;
	}

	@Override
	default String getName() {
		return getConfig().getName();
	}

	LocalCacheConfig getConfig();

	void invalidateSingle(String key);

	void invalidateMultiple(Collection<String> keys);

	Value getSingle(String key);

	Map<String, Value> getMultiple(Collection<String> keys);

	Value putSingle(String key, BiFunction<String, Value, Value> mapper);

	//void putMultiple(Collection<LocalCacheEntry<T>> entries);

	Collection<String> getAllKeys();

}
