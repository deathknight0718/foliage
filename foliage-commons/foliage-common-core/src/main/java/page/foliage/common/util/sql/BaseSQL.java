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
package page.foliage.common.util.sql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import page.foliage.guava.common.collect.ImmutableList;

/**
 * @author Clinton Begin
 * @author Jeff Butler
 * @author Adam Gent
 * @author Kazuki Shimizu
 */
public abstract class BaseSQL<T> {

    // ------------------------------------------------------------------------

    private static final String AND = ") \nAND (", OR = ") \nOR (";

    private final SQLStatement sql = new SQLStatement();

    public abstract T getSelf();

    // ------------------------------------------------------------------------

    public T UPDATE(String table) {
        sql().statementType = StatementType.UPDATE;
        sql().tables.add(table);
        return getSelf();
    }

    public T SET(String sets) {
        sql().sets.add(sets);
        return getSelf();
    }

    public T SET(String... sets) {
        sql().sets.addAll(Arrays.asList(sets));
        return getSelf();
    }

    public T INSERT_INTO(String tableName) {
        sql().statementType = StatementType.INSERT;
        sql().tables.add(tableName);
        return getSelf();
    }

    public T MERGE_INTO(String tableName) {
        sql().statementType = StatementType.MERGE;
        sql().tables.add(tableName);
        return getSelf();
    }

    public T VALUES(String columns, String values) {
        sql().columns.add(columns);
        sql().values.add(values);
        return getSelf();
    }

    public T KEY(String... columns) {
        sql().columns.addAll(Arrays.asList(columns));
        return getSelf();
    }

    public T INTO_COLUMNS(String... columns) {
        sql().columns.addAll(Arrays.asList(columns));
        return getSelf();
    }

    public T INTO_VALUES(String... values) {
        sql().values.addAll(Arrays.asList(values));
        return getSelf();
    }

    public T SELECT(String columns) {
        sql().statementType = StatementType.SELECT;
        sql().select.add(columns);
        return getSelf();
    }

    public T SELECT(String... columns) {
        sql().statementType = StatementType.SELECT;
        sql().select.addAll(Arrays.asList(columns));
        return getSelf();
    }

    public T SELECT_DISTINCT(String columns) {
        sql().distinct = true;
        SELECT(columns);
        return getSelf();
    }

    public T SELECT_DISTINCT(String... columns) {
        sql().distinct = true;
        SELECT(columns);
        return getSelf();
    }

    public T WITH_RECURSIVE(String name, BaseSQL<T> statement1, BaseSQL<T> statement2, BaseSQL<T> statement3) {
        return WITH_RECURSIVE(name, statement1.toString(), statement2.toString(), statement3.toString());
    }

    public T WITH_RECURSIVE(String name, String expression1, String expression2, String expression3) {
        sql().statementType = StatementType.RECURSIVE;
        sql().recursive = name;
        sql().recursiveExpression1 = expression1;
        sql().recursiveExpression2 = expression2;
        sql().recursiveExpression3 = expression3;
        return getSelf();
    }

    public T DELETE_FROM(String table) {
        sql().statementType = StatementType.DELETE;
        sql().tables.add(table);
        return getSelf();
    }

    public T FROM(String table) {
        sql().tables.add(table);
        return getSelf();
    }

    public T FROM(String... tables) {
        sql().tables.addAll(Arrays.asList(tables));
        return getSelf();
    }

    public T JOIN(String join) {
        sql().join.add(join);
        return getSelf();
    }

    public T JOIN(String... joins) {
        sql().join.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T INNER_JOIN(String join) {
        sql().innerJoin.add(join);
        return getSelf();
    }

    public T INNER_JOIN(String... joins) {
        sql().innerJoin.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T LEFT_OUTER_JOIN(String join) {
        sql().leftOuterJoin.add(join);
        return getSelf();
    }

    public T LEFT_OUTER_JOIN(String... joins) {
        sql().leftOuterJoin.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T RIGHT_JOIN(String join) {
        sql().rightJoin.add(join);
        return getSelf();
    }

    public T RIGHT_JOIN(String... joins) {
        sql().rightJoin.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T RIGHT_OUTER_JOIN(String join) {
        sql().rightOuterJoin.add(join);
        return getSelf();
    }

    public T RIGHT_OUTER_JOIN(String... joins) {
        sql().rightOuterJoin.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T OUTER_JOIN(String join) {
        sql().outerJoin.add(join);
        return getSelf();
    }

    public T OUTER_JOIN(String... joins) {
        sql().outerJoin.addAll(Arrays.asList(joins));
        return getSelf();
    }

    public T WHERE(String conditions) {
        sql().where.add(conditions);
        sql().lastList = sql().where;
        return getSelf();
    }

    public T WHERE(String... conditions) {
        sql().where.addAll(Arrays.asList(conditions));
        sql().lastList = sql().where;
        return getSelf();
    }

    public T OR() {
        sql().lastList.add(OR);
        return getSelf();
    }

    public T AND() {
        sql().lastList.add(AND);
        return getSelf();
    }

    public T GROUP_BY(String columns) {
        sql().groupBy.add(columns);
        return getSelf();
    }

    public T GROUP_BY(String... columns) {
        sql().groupBy.addAll(Arrays.asList(columns));
        return getSelf();
    }

    public T HAVING(String conditions) {
        sql().having.add(conditions);
        sql().lastList = sql().having;
        return getSelf();
    }

    public T HAVING(String... conditions) {
        sql().having.addAll(Arrays.asList(conditions));
        sql().lastList = sql().having;
        return getSelf();
    }

    public T ORDER_BY(String columns) {
        sql().orderBy.add(columns);
        return getSelf();
    }

    public T ORDER_BY(String... columns) {
        sql().orderBy.addAll(Arrays.asList(columns));
        return getSelf();
    }

    public T ON_CONFLICT(String... columns) {
        sql().onConflict.addAll(Arrays.asList(columns));
        return getSelf();
    }

    public T DO_UPDATE_SET(String... sets) {
        sql().doUpdateSet.addAll(Arrays.asList(sets));
        return getSelf();
    }

    public T DO_SOMETHING(String expression) {
        sql().doSomething.add(expression);
        return getSelf();
    }

    public T DO_NOTHING() {
        sql().doNothing = true;
        return getSelf();
    }

    public T OFFSET(String symbol) {
        sql().offset = symbol;
        return getSelf();
    }

    public T LIMIT(String symbol) {
        sql().limit = symbol;
        return getSelf();
    }

    // ------------------------------------------------------------------------

    private SQLStatement sql() {
        return sql;
    }

    public <A extends Appendable> A usingAppender(A a) {
        sql().sql(a);
        return a;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sql().sql(sb);
        return sb.toString();
    }

    public String toNormalizeString() {
        return StringUtils.normalizeSpace(toString());
    }

    public String toTemplatedString(StringSubstitutor substitutor) {
        return substitutor.replace(StringUtils.normalizeSpace(toString()));
    }

    // ------------------------------------------------------------------------

    private static class SafeAppendable {

        private final Appendable a;

        private boolean empty = true;

        public SafeAppendable(Appendable a) {
            super();
            this.a = a;
        }

        public SafeAppendable append(CharSequence s) {
            try {
                if (empty && s.length() > 0) {
                    empty = false;
                }
                a.append(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public boolean isEmpty() {
            return empty;
        }

    }

    // ------------------------------------------------------------------------

    private static enum StatementType {
        DELETE, INSERT, SELECT, UPDATE, MERGE, RECURSIVE
    }

    // ------------------------------------------------------------------------

    private static class SQLStatement {

        StatementType statementType;

        List<String> sets = new ArrayList<String>();

        List<String> select = new ArrayList<String>();

        List<String> tables = new ArrayList<String>();

        List<String> join = new ArrayList<String>();

        List<String> innerJoin = new ArrayList<String>();

        List<String> outerJoin = new ArrayList<String>();

        List<String> leftOuterJoin = new ArrayList<String>();

        List<String> rightJoin = new ArrayList<String>();

        List<String> rightOuterJoin = new ArrayList<String>();

        List<String> where = new ArrayList<String>();

        List<String> having = new ArrayList<String>();

        List<String> groupBy = new ArrayList<String>();

        List<String> orderBy = new ArrayList<String>();

        List<String> onConflict = new ArrayList<String>();

        List<String> doSomething = new ArrayList<String>();

        List<String> doUpdateSet = new ArrayList<String>();

        List<String> lastList = new ArrayList<String>();

        List<String> columns = new ArrayList<String>();

        List<String> values = new ArrayList<String>();

        boolean distinct, doNothing;

        String offset, limit;

        String recursive, recursiveExpression1, recursiveExpression2, recursiveExpression3;

        public SQLStatement() {
            // Prevent Synthetic Access
        }

        private void sqlClause(SafeAppendable builder, String keyword, String expression) {
            sqlClause(builder, keyword, ImmutableList.of(expression));
        }

        private void sqlClause(SafeAppendable builder, String keyword, String expression, String open, String close) {
            sqlClause(builder, keyword, ImmutableList.of(expression), open, close, "");
        }

        private void sqlClause(SafeAppendable builder, String keyword, List<String> parts) {
            sqlClause(builder, keyword, parts, "", "", "");
        }

        private void sqlClause(SafeAppendable builder, String keyword, List<String> parts, String open, String close, String conjunction) {
            if (parts.isEmpty()) return;
            if (!builder.isEmpty()) builder.append("\n");
            builder.append(keyword);
            builder.append(" ");
            builder.append(open);
            String last = "________";
            for (int i = 0, n = parts.size(); i < n; i++) {
                String part = parts.get(i);
                if (i > 0 && !part.equals(AND) && !part.equals(OR) && !last.equals(AND) && !last.equals(OR)) {
                    builder.append(conjunction);
                }
                builder.append(part);
                last = part;
            }
            builder.append(close);
        }

        private String recursiveSQL(SafeAppendable builder) {
            sqlClause(builder, "WITH RECURSIVE", recursive, "", " AS (");
            sqlClause(builder, "", recursiveExpression1, "", "");
            sqlClause(builder, "UNION ALL", "");
            sqlClause(builder, "", recursiveExpression2, "", "");
            sqlClause(builder, "", recursiveExpression3, ") ", "");
            return builder.toString();
        }

        private String selectSQL(SafeAppendable builder) {
            if (distinct) sqlClause(builder, "SELECT DISTINCT", select, "", "", ", ");
            else sqlClause(builder, "SELECT", select, "", "", ", ");
            sqlClause(builder, "FROM", tables, "", "", ", ");
            joins(builder);
            sqlClause(builder, "WHERE", where, "(", ")", " AND ");
            sqlClause(builder, "GROUP BY", groupBy, "", "", ", ");
            sqlClause(builder, "HAVING", having, "(", ")", " AND ");
            sqlClause(builder, "ORDER BY", orderBy, "", "", ", ");
            if (offset != null) sqlClause(builder, "OFFSET", offset);
            if (limit != null) sqlClause(builder, "LIMIT", limit);
            return builder.toString();
        }

        private String insertSQL(SafeAppendable builder) {
            sqlClause(builder, "INSERT INTO", tables, "", "", "");
            sqlClause(builder, "", columns, "(", ")", ", ");
            sqlClause(builder, "VALUES", values, "(", ")", ", ");
            sqlClause(builder, "ON CONFLICT", onConflict, "(", ")", ", ");
            if (doNothing) sqlClause(builder, "DO", "NOTHING");
            else if (!doSomething.isEmpty()) sqlClause(builder, "DO", doSomething);
            else if (!doUpdateSet.isEmpty()) sqlClause(builder, "DO UPDATE SET", doUpdateSet, "", "", ", ");
            return builder.toString();
        }

        private String mergeSQL(SafeAppendable builder) {
            sqlClause(builder, "MERGE INTO", tables, "", "", "");
            sqlClause(builder, "KEY", columns, "(", ")", ", ");
            sqlClause(builder, "VALUES", values, "(", ")", ", ");
            return builder.toString();
        }

        private String deleteSQL(SafeAppendable builder) {
            sqlClause(builder, "DELETE FROM", tables, "", "", "");
            sqlClause(builder, "WHERE", where, "(", ")", " AND ");
            return builder.toString();
        }

        private String updateSQL(SafeAppendable builder) {
            sqlClause(builder, "UPDATE", tables, "", "", "");
            joins(builder);
            sqlClause(builder, "SET", sets, "", "", ", ");
            sqlClause(builder, "WHERE", where, "(", ")", " AND ");
            return builder.toString();
        }

        private void joins(SafeAppendable builder) {
            sqlClause(builder, "JOIN", join, "", "", "\nJOIN ");
            sqlClause(builder, "INNER JOIN", innerJoin, "", "", "\nINNER JOIN ");
            sqlClause(builder, "OUTER JOIN", outerJoin, "", "", "\nOUTER JOIN ");
            sqlClause(builder, "LEFT OUTER JOIN", leftOuterJoin, "", "", "\nLEFT OUTER JOIN ");
            sqlClause(builder, "RIGHT JOIN", rightJoin, "", "", "\nRIGHT JOIN ");
            sqlClause(builder, "RIGHT OUTER JOIN", rightOuterJoin, "", "", "\nRIGHT OUTER JOIN ");
        }

        public String sql(Appendable a) {
            SafeAppendable builder = new SafeAppendable(a);
            if (statementType == null) { return null; }
            String answer;
            switch (statementType) {
            case DELETE:
                answer = deleteSQL(builder);
                break;
            case INSERT:
                answer = insertSQL(builder);
                break;
            case MERGE:
                answer = mergeSQL(builder);
                break;
            case SELECT:
                answer = selectSQL(builder);
                break;
            case UPDATE:
                answer = updateSQL(builder);
                break;
            case RECURSIVE:
                answer = recursiveSQL(builder);
                break;
            default:
                answer = null;
            }
            return answer;
        }

    }

}
