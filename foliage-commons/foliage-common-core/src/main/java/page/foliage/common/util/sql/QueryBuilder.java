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

import page.foliage.common.collect.QueryParams;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class QueryBuilder {

    // ------------------------------------------------------------------------

    private final SQLE sqle;

    // ------------------------------------------------------------------------

    public QueryBuilder(SQLE sqle) {
        this.sqle = sqle;
    }

    // ------------------------------------------------------------------------

    public QueryBuilder offset(int offset) {
        sqle.OFFSET(Integer.toString(offset));
        return this;
    }

    public QueryBuilder limit(int limit) {
        sqle.LIMIT(Integer.toString(limit));
        return this;
    }

    // ------------------------------------------------------------------------

    public QueryBuilder orderBy(String expression) {
        sqle.ORDER_BY(expression);
        return this;
    }

    // ------------------------------------------------------------------------

    public QueryBuilder search(String expression) {
        sqle.WHERE(String.format("content_ @@ to_tsquery('%s')", expression));
        return this;
    }

    // ------------------------------------------------------------------------

    public QueryBuilder params(QueryParams params) {
        if (params.containsKey(QueryParams.KEYWORD_OFFSET)) offset(params.offset());
        if (params.containsKey(QueryParams.KEYWORD_LIMIT)) limit(params.limit());
        if (params.containsKey(QueryParams.KEYWORD_ORDER)) for (String order : params.orders()) sqle.ORDER_BY(order);
        if (params.containsKey(QueryParams.KEYWORD_SEARCH)) search(params.first(QueryParams.KEYWORD_SEARCH));
        return this;
    }

    // ------------------------------------------------------------------------

    public SQLE build() {
        return sqle;
    }

}
