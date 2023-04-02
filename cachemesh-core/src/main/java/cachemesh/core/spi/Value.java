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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import cachemesh.common.Serderializer;

public interface Value {

    static final Object            NULL_OBJECT   = new Object();

    static final byte[]            NULL_BYTES    = new byte[] {};

    static final Reference<byte[]> NULL_BYTES_R  = new WeakReference<>(NULL_BYTES);

    static final Reference<Object> NULL_OBJECT_R = new WeakReference<>(NULL_OBJECT);

    boolean hasValue();

    boolean isNullValue();

    <T> T getObject(Serderializer serder, Class<?> valueClass);

    <T> T getObject();

    byte[] getBytes(Serderializer serder);

    byte[] getBytes();

    long getVersion();

}
