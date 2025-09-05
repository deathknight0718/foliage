/*
 * Copyright 2025 Foliage Develop Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package page.foliage.common.util.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import page.foliage.common.collect.QueryParams;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class QueryBuilder {

    // ------------------------------------------------------------------------

    private List<StatementBuilder> operations = new ArrayList<>();

    private List<String> selects = new ArrayList<>(), orders = new ArrayList<>();

    private Integer offset, limit;

    private String search, from;

    // ------------------------------------------------------------------------

    public QueryBuilder() {}

    // ------------------------------------------------------------------------

    public QueryBuilder select(String... columns) {
        selects.addAll(List.of(columns));
        return this;
    }

    // ------------------------------------------------------------------------

    public QueryBuilder from(String from) {
        this.from = from;
        return this;
    }

    // ------------------------------------------------------------------------

    public QueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    // ------------------------------------------------------------------------

    public QueryBuilder orderBy(String expression) {
        this.orders.add(expression);
        return this;
    }

    // ------------------------------------------------------------------------

    public QueryBuilder where(String expression, StatementOperation consumer) {
        operations.add(new StatementBuilder(expression, consumer));
        return this;
    }

    // ------------------------------------------------------------------------

    public QueryBuilder search(String expression) {
        search = String.format("content_ @@ to_tsquery('%s')", expression);
        return this;
    }

    // ------------------------------------------------------------------------

    public QueryBuilder params(QueryParams params) {
        if (params.containsKey(QueryParams.KEYWORD_SEARCH)) search(params.first(QueryParams.KEYWORD_SEARCH));
        if (params.containsKey(QueryParams.KEYWORD_OFFSET)) offset(params.offset());
        if (params.containsKey(QueryParams.KEYWORD_LIMIT)) limit(params.limit());
        if (params.containsKey(QueryParams.KEYWORD_ORDER)) for (String order : params.orders()) orderBy(order);
        return this;
    }

    // ------------------------------------------------------------------------

    public PreparedStatement build(Connection connection) throws SQLException {
        SQLE sqle = new SQLE();
        sqle.SELECT(selects.toArray(String[]::new)).FROM(from);
        if (search != null) sqle.WHERE(search);
        for (StatementBuilder builder : operations) sqle.WHERE(builder.expression);
        if (offset != null && limit != null) sqle.LIMIT(String.valueOf(limit)).OFFSET(String.valueOf(offset));
        if (!orders.isEmpty()) sqle.ORDER_BY(orders.toArray(String[]::new));
        PreparedStatement statement = connection.prepareStatement(sqle.toNormalizeString());
        for (int i = 0; i < operations.size(); i++) operations.get(i).operation.apply(statement, i + 1);
        return statement;
    }

    public PreparedStatement count(Connection connection) throws SQLException {
        SQLE sqle = new SQLE();
        sqle.SELECT("count(1)").FROM(from);
        if (search != null) sqle.WHERE(search);
        for (StatementBuilder builder : operations) sqle.WHERE(builder.expression);
        PreparedStatement statement = connection.prepareStatement(sqle.toNormalizeString());
        for (int i = 0; i < operations.size(); i++) operations.get(i).operation.apply(statement, i + 1);
        return statement;
    }

    // ------------------------------------------------------------------------

    @FunctionalInterface
    public interface StatementOperation {

        void apply(PreparedStatement statement, int index) throws SQLException;

    }

    // ------------------------------------------------------------------------

    public static class StatementBuilder {

        private final String expression;

        private final StatementOperation operation;

        public StatementBuilder(String expression, StatementOperation operation) {
            this.expression = expression;
            this.operation = operation;
        }

    }

}
