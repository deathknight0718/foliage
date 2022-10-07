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
package org.foliage.jdbc.interpret.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.StringJoiner;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.foliage.jdbc.antlr.TSqlLexer;
import org.foliage.jdbc.antlr.TSqlParser;
import org.foliage.jdbc.antlr.TSqlParserBaseListener;
import org.foliage.guava.common.collect.Range;
import org.foliage.jdbc.interpret.Interpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author deathknight0718@qq.com
 * @version 1.0.0
 */
public class H2Interpreter implements Interpreter {

    // ------------------------------------------------------------------------

    public static final String DATABASE_NAME = "H2";

    // ------------------------------------------------------------------------

    private static Logger logger = LoggerFactory.getLogger(H2Interpreter.class);

    // ------------------------------------------------------------------------

    @Override
    public PreparedStatement prepareCapture(Connection connection, String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    public PreparedStatement prepareCapture(Connection connection, String query, String queryIndex) throws SQLException {
        return connection.prepareStatement(new QueryBuilder(query, queryIndex).build());
    }

    @Override
    public PreparedStatement prepareCapture(Connection connection, int resultSetType, int resultSetConcurrency, String query) throws SQLException {
        return connection.prepareStatement(query, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareCapture(Connection connection, int resultSetType, int resultSetConcurrency, String query, String queryIndex) throws SQLException {
        return connection.prepareStatement(new QueryBuilder(query, queryIndex).build(), resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareCaptureRange(Connection connection, String query, String queryIndex) throws SQLException {
        return connection.prepareStatement(new RangeBuilder(query, queryIndex).build());
    }

    // ------------------------------------------------------------------------

    private static class QueryBuilder extends TSqlParserBaseListener {

        private final String query, queryIndex;

        private StringJoiner joiner = new StringJoiner(" ");

        private Range<Integer> range;

        private boolean rewrited;

        public QueryBuilder(String query, String queryIndex) {
            this.query = query;
            this.queryIndex = queryIndex;
        }

        @Override
        public void enterSearch_condition_list(TSqlParser.Search_condition_listContext context) {
            if (this.range == null) this.range = Range.closed(context.start.getTokenIndex(), context.stop.getTokenIndex());
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            if (range == null || !range.contains(node.getSymbol().getTokenIndex())) joiner.add(node.getText());
            else if (!rewrited) {
                joiner.add(MessageFormat.format("{0} >= ? AND {0} <= ?", queryIndex));
                rewrited = true;
            }
        }

        public String build() {
            CharStream stream = CharStreams.fromString(query);
            TSqlLexer lexer = new TSqlLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            TSqlParser parser = new TSqlParser(tokens);
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(this, parser.query_specification());
            if (range == null) joiner.add(MessageFormat.format("WHERE {0} >= ? AND {0} <= ?", queryIndex));
            logger.debug("Rewrite the sql: {}", joiner.toString());
            return joiner.toString();
        }

    }

    // ------------------------------------------------------------------------

    private static class RangeBuilder extends TSqlParserBaseListener {

        private final String query, queryIndex;

        private StringJoiner joiner = new StringJoiner(" ");

        private Range<Integer> range;

        private boolean rewrited;

        public RangeBuilder(String query, String queryIndex) {
            this.query = query;
            this.queryIndex = queryIndex;
        }

        @Override
        public void enterSelect_list(TSqlParser.Select_listContext context) {
            if (this.range == null) this.range = Range.closed(context.start.getTokenIndex(), context.stop.getTokenIndex());
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            if (range == null || !range.contains(node.getSymbol().getTokenIndex())) joiner.add(node.getText());
            else if (!rewrited) {
                joiner.add(MessageFormat.format("MIN({0}), MAX({0})", queryIndex));
                rewrited = true;
            }
        }

        public String build() {
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
