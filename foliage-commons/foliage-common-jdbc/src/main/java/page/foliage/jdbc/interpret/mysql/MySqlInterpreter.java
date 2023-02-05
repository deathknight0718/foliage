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
package page.foliage.jdbc.interpret.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.StringJoiner;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import page.foliage.jdbc.antlr.PlSqlParserBaseListener;
import page.foliage.jdbc.antlr.TSqlLexer;
import page.foliage.jdbc.antlr.TSqlParser;
import page.foliage.jdbc.interpret.Interpreter;

/**
 * 
 * 
 * @author deathknight0718@qq.com
 * @version 1.0.0
 */
public class MySqlInterpreter implements Interpreter {

    // ------------------------------------------------------------------------

    public static final String DATABASE_NAME = "MySQL";

    // ------------------------------------------------------------------------

    private static Logger logger = LoggerFactory.getLogger(MySqlInterpreter.class);

    private static final String SQL_PATTERN_REWRITE_QUERY = "SELECT * FROM ({0}) AS MYSQL_INTERNAL WHERE MYSQL_INTERNAL.{1} >= ? AND MYSQL_INTERNAL.{1} <= ?";

    private static final String SQL_PATTERN_REWRITE_RANGE = "SELECT MIN(MYSQL_INTERNAL.{1}), MAX(MYSQL_INTERNAL.{1}) FROM ({0}) AS MYSQL_INTERNAL";

    // ------------------------------------------------------------------------

    @Override
    public PreparedStatement prepareCapture(Connection connection, String query) throws SQLException {
        String formattedQuery = StringUtils.normalizeSpace(query);
        logger.debug("Rewrited SQL: {}", formattedQuery);
        return connection.prepareStatement(formattedQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }
    
    @Override
    public PreparedStatement prepareCapture(Connection connection, String query, String queryIndex) throws SQLException {
        String formattedQuery = StringUtils.normalizeSpace(query);
        String rewritedQuery = MessageFormat.format(SQL_PATTERN_REWRITE_QUERY, formattedQuery, queryIndex);
        logger.debug("Rewrited SQL: \n{} \n-> {}", formattedQuery, rewritedQuery);
        return connection.prepareStatement(rewritedQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }
    
    @Override
    public PreparedStatement prepareCapture(Connection connection, int resultSetType, int resultSetConcurrency, String query) throws SQLException {
        String formattedQuery = StringUtils.normalizeSpace(query);
        logger.debug("Rewrited SQL: {}", formattedQuery);
        return connection.prepareStatement(formattedQuery, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareCapture(Connection connection, int resultSetType, int resultSetConcurrency, String query, String queryIndex) throws SQLException {
        String formattedQuery = StringUtils.normalizeSpace(query);
        String rewritedQuery = MessageFormat.format(SQL_PATTERN_REWRITE_QUERY, formattedQuery, queryIndex);
        logger.debug("Rewrited SQL: \n{} \n-> {}", formattedQuery, rewritedQuery);
        return connection.prepareStatement(rewritedQuery, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareCaptureRange(Connection connection, String query, String queryIndex) throws SQLException {
        String formattedQuery = StringUtils.normalizeSpace(query);
        String rewritedQuery = MessageFormat.format(SQL_PATTERN_REWRITE_RANGE, formattedQuery, queryIndex);
        logger.debug("Rewrited SQL: \n{} \n-> {}", formattedQuery, rewritedQuery);
        return connection.prepareStatement(rewritedQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unused")
    private static class FormatBuilder extends PlSqlParserBaseListener {

        private final String query;

        private StringJoiner joiner = new StringJoiner(" ");

        private FormatBuilder(String query) {
            this.query = query;
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            joiner.add(node.getText());
        }

        private String build() {
            CharStream stream = CharStreams.fromString(query);
            TSqlLexer lexer = new TSqlLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            TSqlParser parser = new TSqlParser(tokens);
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(this, parser.query_specification());
            logger.debug("Rewrite the sql: {}", joiner.toString());
            return joiner.toString();
        }

    }

}
