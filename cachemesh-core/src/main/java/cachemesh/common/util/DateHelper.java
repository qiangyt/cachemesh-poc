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
package cachemesh.common.util;

import java.time.format.DateTimeFormatter;

public class DateHelper {

    public static final String DAY_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss.SSS";
    public static final String RFC3339_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DAYTIME_PATTERN = DAY_PATTERN + " " + TIME_PATTERN;

    public static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern(DAY_PATTERN);
    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern(TIME_PATTERN);
    public static final DateTimeFormatter RFC3339 = DateTimeFormatter.ofPattern(RFC3339_PATTERN);
    public static final DateTimeFormatter DAYTIME = DateTimeFormatter.ofPattern(DAYTIME_PATTERN);
}
