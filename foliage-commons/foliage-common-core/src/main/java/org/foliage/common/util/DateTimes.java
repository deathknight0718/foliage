/*******************************************************************************
 * Copyright 2022 deathknight0718@qq.com.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.foliage.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class DateTimes {

    // ------------------------------------------------------------------------

    public static final LocalDateTime EPOCH = Instant.EPOCH.atZone(ZoneId.systemDefault()).toLocalDateTime();

    public static final DateTimeFormatter TIME_FORMATTER_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    public static final DateTimeFormatter TIME_FORMATTER_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ------------------------------------------------------------------------

    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // ------------------------------------------------------------------------

    public static LocalDate dateOf(String input) {
        return dateOf(input, TIME_FORMATTER_DATE);
    }

    public static LocalDate dateOf(String input, DateTimeFormatter formatter) {
        return LocalDate.parse(input, formatter);
    }

    public static LocalDateTime dateTimeOf(String input) {
        return dateTimeOf(input, TIME_FORMATTER_DATE_TIME);
    }

    public static LocalDateTime dateTimeOf(String input, DateTimeFormatter formatter) {
        return LocalDateTime.parse(input, formatter);
    }

    // ------------------------------------------------------------------------

    public static String format(LocalDate temporal) {
        return format(temporal, TIME_FORMATTER_DATE);
    }

    public static String format(LocalDateTime temporal) {
        return format(temporal, TIME_FORMATTER_DATE_TIME);
    }

    public static String format(TemporalAccessor temporal, DateTimeFormatter formatter) {
        if (temporal == null) return null;
        return formatter.format(temporal);
    }

}
