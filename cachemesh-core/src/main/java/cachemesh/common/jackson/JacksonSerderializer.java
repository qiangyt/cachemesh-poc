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
package cachemesh.common.jackson;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cachemesh.common.Serderializer;
import lombok.Getter;

@Getter
public class JacksonSerderializer implements Serderializer {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final JacksonSerderializer DEFAULT = new JacksonSerderializer();

    private final Jackson jackson;

    private final Charset charset;

    public JacksonSerderializer() {
        this(Jackson.DEFAULT, DEFAULT_CHARSET);
    }

    public JacksonSerderializer(Jackson jackson, Charset charset) {
        this.jackson = jackson;
        this.charset = charset;
    }

    @Override
    public byte[] serialize(Object obj) {
        return this.jackson.toBytes(obj);
    }

    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return this.jackson.from(bytes, clazz);
    }

}
