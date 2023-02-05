/*******************************************************************************
 * Copyright 2020 Deathknight0718@qq.com.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package page.foliage.jdbc.type;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.BoundType;
import page.foliage.guava.common.collect.Range;

/**
 * 带有 JDBC 类型检查的 Range 生成工具，进行了严格的类型检查
 * 
 * @author deathknight0718@qq.com
 * @version 1.0.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TypeRanges {

    // ------------------------------------------------------------------------

    // @see: JSR-221: Data Type Conversion Tables

    // ### TABLE B-3: JDBC Types Mapped to Java Object Types

    // ResultSet.getObject and CallableStatement.getObject use the mapping shown in TABLE B-3 for standard mappings.

    // | JDBC Type     | Java Type                  |
    // |---------------|----------------------------|
    // | NUMERIC       | java.math.BigDecimal       |
    // | DECIMAL       | java.math.BigDecimal       |
    // | INTEGER       | int                        |
    // | BIGINT        | long                       |
    // | DATE          | java.sql.Date              |
    // | TIMESTAMP     | java.sql.Timestamp         |

    // @see: JSR-221: Data Type Conversion Tables

    // ### TABLE B-4: Java Object Types Mapped to JDBC Types

    // PreparedStatement.setObject, PreparedStatement.setNull, RowSet.setNull and RowSet.setObject use the mapping shown TABLE B-4 when no parameter specifying a target JDBC type is provided.

    // | Java Object Type         | JDBC Type                                                   |
    // |--------------------------|-------------------------------------------------------------|
    // | java.math.BigDecimal     | NUMERIC                                                     |
    // | Integer                  | INTEGER                                                     |
    // | Long                     | BIGINT                                                      |
    // | java.math.BigInteger     | BIGINT                                                      |
    // | java.sql.Date            | DATE                                                        |
    // | java.sql.Timestamp       | TIMESTAMP                                                   |
    // | java.util.Calendar       | TIMESTAMP                                                   |
    // | java.util.Date           | TIMESTAMP                                                   |
    // | java.time.LocalDate      | DATE                                                        |
    // | java.time.LocalDateTime  | TIMESTAMP                                                   |
    // | java.time.OffsetDatetime | TIMESTAMP_WITH_TIMEZONE                                     |

    // ------------------------------------------------------------------------

    public static TypeRange greaterThan(JDBCType jdbcType, Object value) {
        Preconditions.checkNotNull(value);
        if (value instanceof Long) return greaterThanWithLong(jdbcType, (Long) value);
        else if (value instanceof LocalDateTime) return greaterThanWithLocalDateTime(jdbcType, (LocalDateTime) value);
        else if (value instanceof Integer) return greaterThanWithInteger(jdbcType, (Integer) value);
        else if (value instanceof BigDecimal) return greaterThanWithBigDecimal(jdbcType, (BigDecimal) value);
        else if (value instanceof Date) return greaterThanWithDate(jdbcType, (Date) value);
        else if (value instanceof Timestamp) return greaterThanWithTimestamp(jdbcType, (Timestamp) value);
        else throw new IllegalArgumentException("Failed to invalid class name: " + value.getClass());
    }

    private static TypeRange greaterThanWithLong(JDBCType jdbcType, long value) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.greaterThan(BigDecimal.valueOf(value)));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.greaterThan((int) value));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.greaterThan(value));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange greaterThanWithLocalDateTime(JDBCType jdbcType, LocalDateTime value) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.greaterThan(Date.valueOf(value.toLocalDate())));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.greaterThan(Timestamp.valueOf(value)));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange greaterThanWithInteger(JDBCType jdbcType, int value) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.greaterThan(BigDecimal.valueOf(value)));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.greaterThan(value));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.greaterThan((long) value));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange greaterThanWithBigDecimal(JDBCType jdbcType, BigDecimal value) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.greaterThan(value));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.greaterThan(value.intValue()));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.greaterThan(value.longValue()));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange greaterThanWithDate(JDBCType jdbcType, Date value) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.greaterThan(value));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.greaterThan(new Timestamp(value.getTime())));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange greaterThanWithTimestamp(JDBCType jdbcType, Timestamp value) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.greaterThan(new Date(value.getTime())));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.greaterThan(value));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    // ------------------------------------------------------------------------

    public static TypeRange atLeast(JDBCType jdbcType, Object value) {
        Preconditions.checkNotNull(value);
        if (value instanceof Long) return atLeastWithLong(jdbcType, (Long) value);
        else if (value instanceof LocalDateTime) return atLeastWithLocalDateTime(jdbcType, (LocalDateTime) value);
        else if (value instanceof Integer) return atLeastWithInteger(jdbcType, (Integer) value);
        else if (value instanceof BigDecimal) return atLeastWithBigDecimal(jdbcType, (BigDecimal) value);
        else if (value instanceof Date) return atLeastWithDate(jdbcType, (Date) value);
        else if (value instanceof Timestamp) return atLeastWithTimestamp(jdbcType, (Timestamp) value);
        else throw new IllegalArgumentException("Failed to invalid class name: " + value.getClass());
    }

    private static TypeRange atLeastWithLong(JDBCType jdbcType, long value) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.atLeast(BigDecimal.valueOf(value)));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.atLeast((int) value));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.atLeast(value));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange atLeastWithLocalDateTime(JDBCType jdbcType, LocalDateTime value) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.atLeast(Date.valueOf(value.toLocalDate())));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.atLeast(Timestamp.valueOf(value)));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange atLeastWithInteger(JDBCType jdbcType, int value) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.atLeast(BigDecimal.valueOf(value)));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.atLeast(value));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.atLeast((long) value));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange atLeastWithBigDecimal(JDBCType jdbcType, BigDecimal value) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.atLeast(value));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.atLeast(value.intValue()));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.atLeast(value.longValue()));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange atLeastWithDate(JDBCType jdbcType, Date value) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.atLeast(value));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.atLeast(new Timestamp(value.getTime())));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange atLeastWithTimestamp(JDBCType jdbcType, Timestamp value) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.atLeast(new Date(value.getTime())));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.atLeast(value));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    // ------------------------------------------------------------------------

    public static TypeRange closed(JDBCType jdbcType, Object lower, Object upper) {
        Preconditions.checkNotNull(lower);
        Preconditions.checkNotNull(upper);
        if (lower instanceof Long && upper instanceof Long) return closedWithLong(jdbcType, (Long) lower, (Long) upper);
        else if (lower instanceof LocalDateTime && upper instanceof LocalDateTime) return closedWithLocalDateTime(jdbcType, (LocalDateTime) lower, (LocalDateTime) upper);
        else if (lower instanceof Integer && upper instanceof Integer) return closedWithInteger(jdbcType, (Integer) lower, (Integer) upper);
        else if (lower instanceof BigDecimal && upper instanceof BigDecimal) return closedWithBigDecimal(jdbcType, (BigDecimal) lower, (BigDecimal) upper);
        else if (lower instanceof Date && upper instanceof Date) return closedWithDate(jdbcType, (Date) lower, (Date) upper);
        else if (lower instanceof Timestamp && upper instanceof Timestamp) return closedWithTimestamp(jdbcType, (Timestamp) lower, (Timestamp) upper);
        else throw new IllegalArgumentException("Failed to invalid class name: " + lower.getClass() + " and " + upper.getClass());
    }

    private static TypeRange closedWithLong(JDBCType jdbcType, long lower, long upper) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.closed(BigDecimal.valueOf(lower), BigDecimal.valueOf(upper)));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.closed((int) lower, (int) upper));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.closed(lower, upper));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange closedWithLocalDateTime(JDBCType jdbcType, LocalDateTime lower, LocalDateTime upper) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.closed(Date.valueOf(lower.toLocalDate()), Date.valueOf(upper.toLocalDate())));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.closed(Timestamp.valueOf(lower), Timestamp.valueOf(upper)));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange closedWithInteger(JDBCType jdbcType, int lower, int upper) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.closed(BigDecimal.valueOf(lower), BigDecimal.valueOf(upper)));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.closed(lower, upper));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.closed((long) lower, (long) upper));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange closedWithBigDecimal(JDBCType jdbcType, BigDecimal lower, BigDecimal upper) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.closed(lower, upper));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.closed(lower.intValue(), upper.intValue()));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.closed(lower.longValue(), upper.longValue()));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange closedWithDate(JDBCType jdbcType, Date lower, Date upper) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.closed(lower, upper));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.closed(new Timestamp(lower.getTime()), new Timestamp(upper.getTime())));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange closedWithTimestamp(JDBCType jdbcType, Timestamp lower, Timestamp upper) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.closed(new Date(lower.getTime()), new Date(upper.getTime())));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.closed(lower, upper));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    // ------------------------------------------------------------------------

    public static TypeRange singleton(JDBCType jdbcType, Comparable value) {
        Preconditions.checkNotNull(value);
        if (value instanceof Long) return singletonWithLong(jdbcType, (Long) value);
        else if (value instanceof LocalDateTime) return singletonLocalDateTime(jdbcType, (LocalDateTime) value);
        else if (value instanceof Integer) return singletonWithInteger(jdbcType, (Integer) value);
        else if (value instanceof BigDecimal) return singletonWithBigDecimal(jdbcType, (BigDecimal) value);
        else if (value instanceof Date) return singletonWithDate(jdbcType, (Date) value);
        else if (value instanceof Timestamp) return singletonWithTimestamp(jdbcType, (Timestamp) value);
        else throw new IllegalArgumentException("Failed to invalid class name: " + value.getClass());
    }

    private static TypeRange singletonWithLong(JDBCType jdbcType, long value) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.singleton(BigDecimal.valueOf(value)));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.singleton((int) value));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.singleton(value));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange singletonLocalDateTime(JDBCType jdbcType, LocalDateTime value) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.singleton(Date.valueOf(value.toLocalDate())));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.singleton(Timestamp.valueOf(value)));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange singletonWithInteger(JDBCType jdbcType, int value) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.singleton(BigDecimal.valueOf(value)));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.singleton(value));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.singleton((long) value));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange singletonWithBigDecimal(JDBCType jdbcType, BigDecimal value) {
        switch (jdbcType) {
        case NUMERIC:
        case DECIMAL:
            return new BigDecimalTypeRange(jdbcType, Range.singleton(value));
        case INTEGER:
            return new IntTypeRange(jdbcType, Range.singleton(value.intValue()));
        case BIGINT:
            return new LongTypeRange(jdbcType, Range.singleton(value.longValue()));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange singletonWithDate(JDBCType jdbcType, Date value) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.singleton(value));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.singleton(new Timestamp(value.getTime())));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    private static TypeRange singletonWithTimestamp(JDBCType jdbcType, Timestamp value) {
        switch (jdbcType) {
        case DATE:
            return new DateTypeRange(jdbcType, Range.singleton(new Date(value.getTime())));
        case TIMESTAMP:
            return new TimestampTypeRange(jdbcType, Range.singleton(value));
        default:
            throw new IllegalArgumentException("Failed to invalid jdbc type: " + jdbcType);
        }
    }

    // ------------------------------------------------------------------------

    public static abstract class BaseTypeRange<T extends Comparable<?>> implements TypeRange {

        private static final long serialVersionUID = 1L;

        private final JDBCType jdbcType;

        private final Range<T> delegate;

        private BaseTypeRange(JDBCType jdbcType, Range<T> delegate) {
            this.jdbcType = jdbcType;
            this.delegate = delegate;
        }

        protected Range delegate() {
            return delegate;
        }

        @Override
        public boolean isConnected(TypeRange other) {
            Preconditions.checkArgument(jdbcType == other.jdbcType());
            BaseTypeRange range = (BaseTypeRange) other;
            return delegate.isConnected(range.delegate);
        }

        @Override
        public boolean isSingleton() {
            return Objects.equals(lower(), upper());
        }

        public JDBCType jdbcType() {
            return jdbcType;
        }

        @Override
        public T lower() {
            return delegate.lowerEndpoint();
        }

        @Override
        public T upper() {
            return delegate.upperEndpoint();
        }

        @Override
        public BoundType lowerType() {
            return delegate.lowerBoundType();
        }

        @Override
        public BoundType upperType() {
            return delegate.upperBoundType();
        }

        @Override
        public String toString() {
            return delegate.toString();
        }

    }

    // ------------------------------------------------------------------------

    public static class IntTypeRange extends BaseTypeRange<Integer> {

        private static final long serialVersionUID = 1L;

        public IntTypeRange(JDBCType jdbcType, Range<Integer> delegate) {
            super(jdbcType, delegate);
        }

        @Override
        public IntTypeRange interval(Comparable interval) {
            Preconditions.checkArgument(interval instanceof Long);
            Range<Integer> range = Range.range(lower(), lowerType(), lower() + (int) (long) interval, upperType());
            return new IntTypeRange(jdbcType(), range);
        }

        @Override
        public IntTypeRange intersection(TypeRange other) {
            Preconditions.checkArgument(jdbcType() == other.jdbcType());
            IntTypeRange range = (IntTypeRange) other;
            return new IntTypeRange(jdbcType(), delegate().intersection(range.delegate()));
        }

    }

    public static class LongTypeRange extends BaseTypeRange<Long> {

        private static final long serialVersionUID = 1L;

        public LongTypeRange(JDBCType jdbcType, Range<Long> delegate) {
            super(jdbcType, delegate);
        }

        @Override
        public LongTypeRange interval(Comparable interval) {
            Preconditions.checkArgument(interval instanceof Long);
            Range<Long> range = Range.range(lower(), lowerType(), lower() + (long) interval, upperType());
            return new LongTypeRange(jdbcType(), range);
        }

        @Override
        public LongTypeRange intersection(TypeRange other) {
            Preconditions.checkArgument(jdbcType() == other.jdbcType());
            LongTypeRange range = (LongTypeRange) other;
            return new LongTypeRange(jdbcType(), delegate().intersection(range.delegate()));
        }

    }

    public static class BigDecimalTypeRange extends BaseTypeRange<BigDecimal> {

        private static final long serialVersionUID = 1L;

        public BigDecimalTypeRange(JDBCType jdbcType, Range<BigDecimal> delegate) {
            super(jdbcType, delegate);
        }

        @Override
        public BigDecimalTypeRange interval(Comparable interval) {
            Preconditions.checkArgument(interval instanceof Long);
            Range<BigDecimal> range = Range.range(lower(), lowerType(), lower().add(BigDecimal.valueOf((long) interval)), upperType());
            return new BigDecimalTypeRange(jdbcType(), range);
        }

        @Override
        public BigDecimalTypeRange intersection(TypeRange other) {
            Preconditions.checkArgument(jdbcType() == other.jdbcType());
            BigDecimalTypeRange range = (BigDecimalTypeRange) other;
            return new BigDecimalTypeRange(jdbcType(), delegate().intersection(range.delegate()));
        }

    }

    public static class DateTypeRange extends BaseTypeRange<Date> {

        private static final long serialVersionUID = 1L;

        public DateTypeRange(JDBCType jdbcType, Range<Date> delegate) {
            super(jdbcType, delegate);
        }

        @Override
        public DateTypeRange interval(Comparable interval) {
            Preconditions.checkArgument(interval instanceof Duration);
            Date upper = new Date(lower().toInstant().plus((Duration) interval).toEpochMilli());
            return new DateTypeRange(jdbcType(), Range.range(lower(), lowerType(), upper, upperType()));
        }

        @Override
        public DateTypeRange intersection(TypeRange other) {
            Preconditions.checkArgument(jdbcType() == other.jdbcType());
            DateTypeRange range = (DateTypeRange) other;
            return new DateTypeRange(jdbcType(), delegate().intersection(range.delegate()));
        }

    }

    public static class TimestampTypeRange extends BaseTypeRange<Timestamp> {

        private static final long serialVersionUID = 1L;

        public TimestampTypeRange(JDBCType jdbcType, Range<Timestamp> delegate) {
            super(jdbcType, delegate);
        }

        @Override
        public TimestampTypeRange interval(Comparable interval) {
            Preconditions.checkArgument(interval instanceof Duration);
            Timestamp upper = Timestamp.from(lower().toInstant().plus((Duration) interval));
            return new TimestampTypeRange(jdbcType(), Range.range(lower(), lowerType(), upper, upperType()));
        }

        @Override
        public TimestampTypeRange intersection(TypeRange other) {
            Preconditions.checkArgument(jdbcType() == other.jdbcType());
            TimestampTypeRange range = (TimestampTypeRange) other;
            return new TimestampTypeRange(jdbcType(), delegate().intersection(range.delegate()));
        }

    }

}
