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

import java.time.Duration;

import java.time.Instant;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;

public class DurationHelper {

    private static final Pattern PERIOD_PATTERN = Pattern.compile("([0-9]+)([smhdw])");

    public static Duration parse(String text) {
        if (StringHelper.isBlank(text)) {
            return null;
        }
        if (Character.isDigit(text.charAt(0)) == false) {
            return Duration.parse(text);
        }

        text = text.toLowerCase(Locale.ENGLISH);

        Matcher matcher = PERIOD_PATTERN.matcher(text);
        Instant i = Instant.EPOCH;
        while (matcher.find()) {
            int num = Integer.parseInt(matcher.group(1));
            String typ = matcher.group(2);
            switch (typ) {
            case "s":
                i = i.plus(Duration.ofSeconds(num));
                break;
            case "m":
                i = i.plus(Duration.ofMinutes(num));
                break;
            case "h":
                i = i.plus(Duration.ofHours(num));
                break;
            case "d":
                i = i.plus(Duration.ofDays(num));
                break;
            case "w":
                i = i.plus(Period.ofWeeks(num));
                break;
            }
        }

        return Duration.ofMillis(i.toEpochMilli());
    }

}
