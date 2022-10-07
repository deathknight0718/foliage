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
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.TextStringBuilder;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.matcher.StringMatcher;
import org.apache.commons.text.matcher.StringMatcherFactory;
import org.foliage.guava.common.collect.Lists;
import org.foliage.jdbc.type.TypeRange;
import org.foliage.jdbc.type.TypeRanges;
import org.foliage.jdbc.type.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author deathknight0718@qq.com
 * @version 1.0.0
 */
public interface Interpreter {

    // ------------------------------------------------------------------------

    Logger logger = LoggerFactory.getLogger(Interpreter.class);

    StringMatcher DEFFAULT_PREFIX = StringMatcherFactory.INSTANCE.stringMatcher("%{");

    StringMatcher DEFFAULT_SUFFIX = StringMatcherFactory.INSTANCE.stringMatcher("}");

    // ------------------------------------------------------------------------

    PreparedStatement prepareCapture(Connection connection, String query) throws SQLException;

    PreparedStatement prepareCapture(Connection connection, String query, String queryIndex) throws SQLException;

    PreparedStatement prepareCapture(Connection connection, int resultSetType, int resultSetConcurrentType, String query) throws SQLException;

    PreparedStatement prepareCapture(Connection connection, int resultSetType, int resultSetConcurrentType, String query, String queryIndex) throws SQLException;

    default PreparedStatement prepareCaptureInject(PreparedStatement statement, TypeRange range) throws SQLException {
        TypeUtils.injectObjectParameters(statement, range.lower(), range.upper());
        return statement;
    }

    // ------------------------------------------------------------------------

    PreparedStatement prepareCaptureRange(Connection connection, String query, String queryIndex) throws SQLException;

    default TypeRange prepareCaptureRangeExecute(PreparedStatement statement, JDBCType jdbcType) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) throw new SQLException("Error! cannot found the result in query range.");
            Object lower = TypeUtils.extractRangeTypedValue(resultSet, jdbcType, 1);
            Object upper = TypeUtils.extractRangeTypedValue(resultSet, jdbcType, 2);
            // 数据库为 MySQL 时，ResultSet#next 返回 true ， lower 和 upper 为空
            if (lower == null || upper == null) return null;
            // 获取数据索引的全集
            return TypeRanges.closed(jdbcType, lower, upper);
        }
    }

    // ------------------------------------------------------------------------

    default PreparedStatement prepareTemplatePut(Connection connection, String preparedTemplate) throws SQLException {
        String rewritedSql = new StringSubstitutor((StringLookup) null, DEFFAULT_PREFIX, DEFFAULT_SUFFIX, '%') {

            @Override
            protected String resolveVariable(String variableName, TextStringBuilder buf, int startPos, int endPos) {
                return "?";
            }

        }.replace(preparedTemplate);
        logger.debug("Interpretted SQL: \n{} \n-> {}", preparedTemplate, rewritedSql);
        connection.setAutoCommit(false); // 取消自动提交设置
        return connection.prepareStatement(rewritedSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    // ------------------------------------------------------------------------

    default List<String> extractParameterOrder(String preparedTemplate) {
        List<String> variableOrder = Lists.newArrayList();
        new StringSubstitutor((StringLookup) null, DEFFAULT_PREFIX, DEFFAULT_SUFFIX, '%') {

            @Override
            protected String resolveVariable(String variableName, TextStringBuilder buf, int startPos, int endPos) {
                variableOrder.add(variableName.toLowerCase());
                return super.resolveVariable(variableName, buf, startPos, endPos);
            }

        }.replace(preparedTemplate);
        return variableOrder;
    }

}
