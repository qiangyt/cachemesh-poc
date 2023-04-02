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
package cachemesh.core;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import cachemesh.common.Serderializer;
import cachemesh.core.spi.Value;

public class ValueImpl implements Value {

    private Reference<byte[]> bytesR; // TODO: or use SoftReference; have this configurable

    private Object            object;

    private long              version;

    public ValueImpl(byte[] bytes, long version) {
        withBytes(bytes, version);
    }

    void withBytes(byte[] bytes, long version) {
        if (bytes == null || bytes == NULL_BYTES) {
            this.bytesR = NULL_BYTES_R;
            this.object = NULL_OBJECT;
        } else {
            this.bytesR = new WeakReference<>(bytes);
            this.object = null;
        }

        this.version = version;
    }

    public ValueImpl(Object obj, long version) {
        withObject(obj, version);
    }

    void withObject(Object obj, long version) {
        if (obj == null || obj == NULL_OBJECT) {
            this.bytesR = NULL_BYTES_R;
            this.object = NULL_OBJECT;
        } else {
            this.bytesR = null;
            this.object = obj;
        }

        this.version = version;
    }

    public ValueImpl(Object obj, byte[] bytes, long version) {
        withBytes(bytes, version);
        withObject(obj, version);
    }

    @Override
    public boolean isNullValue() {
        return (this.object == NULL_OBJECT || this.bytesR == NULL_BYTES_R);
    }

    /**
     * return false means: TODO
     */
    @Override
    public boolean hasValue() {
        return (this.object != null || this.bytesR != null);
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject() {
        var obj = this.object;
        if (obj == NULL_OBJECT) {
            return null;
        }
        return (T) obj;
    }

    @Override
    public byte[] getBytes() {
        var biR = this.bytesR;
        if (biR == NULL_BYTES_R) {
            return null;
        }
        return (biR == null) ? null : biR.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject(Serderializer serder, Class<?> valueClass) {
        var obj = this.object;
        var biR = this.bytesR;

        if (obj == NULL_OBJECT) {
            return null;
        }
        if (obj != null) {
            return (T) obj;
        }

        if (biR == NULL_BYTES_R) {
            this.object = NULL_OBJECT;
            return null;
        }

        byte[] bytes = (biR == null) ? null : biR.get();
        if (bytes == null) {
            return null;
        }
        this.object = obj = serder.deserialize(bytes, valueClass);
        return (T) obj;
    }

    @Override
    public byte[] getBytes(Serderializer serder) {
        var biR = this.bytesR;
        var obj = this.object;

        if (biR == NULL_BYTES_R) {
            return null;
        }
        byte[] bytes = (biR == null) ? null : biR.get();
        if (bytes != null) {
            return bytes;
        }

        if (obj == NULL_OBJECT) {
            this.bytesR = NULL_BYTES_R;
            return null;
        }
        if (obj == null) {
            return null;
        }

        bytes = serder.serialize(obj);
        this.bytesR = new WeakReference<>(bytes);

        return bytes;
    }

}
