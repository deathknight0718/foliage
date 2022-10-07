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
package org.foliage.jdbc.interpret;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.foliage.guava.common.collect.ImmutableMap;
import org.foliage.jdbc.interpret.h2.H2Interpreter;
import org.foliage.jdbc.interpret.mysql.MySqlInterpreter;
import org.foliage.jdbc.interpret.oracle.OracleInterpreter;
import org.foliage.jdbc.interpret.phoenix.PhoenixInterpreter;
import org.foliage.jdbc.interpret.postgresql.PostgreSqlInterpreter;
import org.foliage.jdbc.interpret.sqlserver.SqlServerInterpreter;

/**
 * 
 * 
 * @author 1111395@greatwall.com
 * @version 1.0.0
 */
public class Interpreters {

    // ------------------------------------------------------------------------

    public final static Interpreter STATEMENT_BUILDER_H2 = new H2Interpreter();

    public final static Interpreter STATEMENT_BUILDER_ORACLE = new OracleInterpreter();

    public final static Interpreter STATEMENT_BUILDER_POSTGRE_SQL = new PostgreSqlInterpreter();

    public final static Interpreter STATEMENT_BUILDER_SQL_SERVER = new SqlServerInterpreter();

    public final static Interpreter STATEMENT_BUILDER_MYSQL = new MySqlInterpreter();

    public final static Interpreter STATEMENT_BUILDER_PHOENIX = new PhoenixInterpreter();

    public final static Map<String, Interpreter> STATEMENT_BUILDER_MAP = ImmutableMap.<String, Interpreter>builder() //
        .put(H2Interpreter.DATABASE_NAME, STATEMENT_BUILDER_H2) //
        .put(OracleInterpreter.DATABASE_NAME, STATEMENT_BUILDER_ORACLE) //
        .put(PostgreSqlInterpreter.DATABASE_NAME, STATEMENT_BUILDER_POSTGRE_SQL) //
        .put(SqlServerInterpreter.DATABASE_NAME, STATEMENT_BUILDER_SQL_SERVER) //
        .put(MySqlInterpreter.DATABASE_NAME, STATEMENT_BUILDER_MYSQL) //
        .put(PhoenixInterpreter.DATABASE_NAME, STATEMENT_BUILDER_PHOENIX) //
        .build();

    public static Interpreter builder(Connection connection) throws SQLException {
        return builder(connection.getMetaData());
    }

    public static Interpreter builder(DatabaseMetaData metaData) throws SQLException {
        return STATEMENT_BUILDER_MAP.getOrDefault(metaData.getDatabaseProductName(), STATEMENT_BUILDER_H2);
    }

}
