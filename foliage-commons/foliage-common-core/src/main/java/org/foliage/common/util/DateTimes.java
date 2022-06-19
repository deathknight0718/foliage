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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
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

    public static final DateTimeFormatter TIME_FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    public static final DateTimeFormatter TIME_FORMATTER_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public static final DateTimeFormatter TIME_FORMATTER_WITH_T = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").withZone(ZoneId.systemDefault());

    // ------------------------------------------------------------------------

    public static LocalDateTime toLocalDateTime(Date date) {
        return toLocalDateTime(date, null);
    }

    public static LocalDateTime toLocalDateTime(Date date, LocalDateTime defaultTime) {
        if (date == null) return defaultTime;
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

    public static String format(Timestamp temporal) {
        return format(temporal.toLocalDateTime());
    }

    public static String format(java.sql.Date temporal) {
        return format(temporal.toLocalDate());
    }

    public static String format(TemporalAccessor temporal, DateTimeFormatter formatter) {
        if (temporal == null) return null;
        return formatter.format(temporal);
    }

    // ------------------------------------------------------------------------

    public static Date formatStr2Date(String dateStr, DateTimeFormatter format) {
        LocalDate localDate = dateOf(dateStr, format);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    public static Date getAddDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(5, n);
        return cal.getTime();
    }

    public static Date getAddMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(2, n);
        return cal.getTime();
    }

}
