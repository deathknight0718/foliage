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

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 
 * @author deathknight0718@qq.com
 * @version 1.0.0
 */
public class TypeUtils {

    // ------------------------------------------------------------------------

    public static JDBCType extractJdbcType(ResultSetMetaData resultSetMetaData, String name) throws SQLException {
        int count = resultSetMetaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String label = StringUtils.lowerCase(resultSetMetaData.getColumnLabel(i));
            if (StringUtils.equalsIgnoreCase(label, name)) return JDBCType.valueOf(resultSetMetaData.getColumnType(i));
        }
        throw new IllegalArgumentException("Cannot found the index field type.");
    }

    public static Object extractRangeTypedValue(ResultSet resultSet, JDBCType indexType, String label) throws SQLException {
        switch (indexType) {
            case INTEGER:
            case BIGINT:
            case NUMERIC:
            case DECIMAL:
                return resultSet.getLong(label);
            case DATE:
            case TIMESTAMP:
                return resultSet.getTimestamp(label);
            default:
                throw new IllegalArgumentException("Failed to access jdbc type: " + indexType + " of label: " + label);
        }
    }

    public static Object extractRangeTypedValue(ResultSet resultSet, JDBCType indexType, int index) throws SQLException {
        switch (indexType) {
            case INTEGER:
            case BIGINT:
            case NUMERIC:
            case DECIMAL:
                return resultSet.getLong(index);
            case DATE:
            case TIMESTAMP:
                return resultSet.getTimestamp(index);
            default:
                throw new IllegalArgumentException("Failed to access jdbc type: " + indexType + " of index: " + index);
        }
    }

    // ------------------------------------------------------------------------

    // @see: JSR-221: Data Type Conversion Tables

    // ### TABLE B-4: Java Object Types Mapped to JDBC Types

    // PreparedStatement.setObject, PreparedStatement.setNull, RowSet.setNull and RowSet.setObject use the mapping shown TABLE B-4 when no parameter specifying a target JDBC type is provided.

    // | Java Object Type         | JDBC Type                                                   |
    // |--------------------------|-------------------------------------------------------------|
    // | String                   | CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR or LONGNVARCHAR |
    // | java.math.BigDecimal     | NUMERIC                                                     |
    // | Boolean                  | BIT or BOOLEAN                                              |
    // | Byte                     | TINYINT                                                     |
    // | Short                    | SMALLINT                                                    |
    // | Integer                  | INTEGER                                                     |
    // | Long                     | BIGINT                                                      |
    // | Float                    | REAL                                                        |
    // | Double                   | DOUBLE                                                      |
    // | byte[]                   | BINARY, VARBINARY, or LONGVARBINARY                         |
    // | java.math.BigInteger     | BIGINT                                                      |
    // | java.sql.Date            | DATE                                                        |
    // | java.sql.Time            | TIME                                                        |
    // | java.sql.Timestamp       | TIMESTAMP                                                   |
    // | java.sql.Clob            | CLOB                                                        |
    // | java.sql.Blob            | BLOB                                                        |
    // | java.sql.Array           | ARRAY                                                       |
    // | java.sql.Struct          | STRUCT                                                      |
    // | java.sql.Ref             | REF                                                         |
    // | java.net.URL             | DATALINK                                                    |
    // | Java class               | JAVA_OBJECT                                                 |
    // | java.sql.RowId           | ROWID                                                       |
    // | java.sql.NClob           | NCLOB                                                       |
    // | java.sql.SQLXML          | SQLXML                                                      |
    // | java.util.Calendar       | TIMESTAMP                                                   |
    // | java.util.Date           | TIMESTAMP                                                   |
    // | java.time.LocalDate      | DATE                                                        |
    // | java.time.LocalTime      | TIME                                                        |
    // | java.time.LocalDateTime  | TIMESTAMP                                                   |
    // | java.time.OffsetTime     | TIME_WITH_TIMEZONE                                          |
    // | java.time.OffsetDatetime | TIMESTAMP_WITH_TIMEZONE                                     |

    public static PreparedStatement injectObjectParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
        return statement;
    }

}
