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
package page.foliage.jdbc.interpret.oracle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import page.foliage.common.util.sql.SQLE;
import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.guava.common.collect.Maps;
import page.foliage.guava.common.collect.Range;
import page.foliage.jdbc.antlr.CaseChangingCharStream;
import page.foliage.jdbc.antlr.PlSqlLexer;
import page.foliage.jdbc.antlr.PlSqlParser;
import page.foliage.jdbc.antlr.PlSqlParser.Insert_statementContext;
import page.foliage.jdbc.antlr.PlSqlParser.Single_table_insertContext;
import page.foliage.jdbc.antlr.PlSqlParser.Tableview_nameContext;
import page.foliage.jdbc.antlr.PlSqlParser.Update_set_clauseContext;
import page.foliage.jdbc.antlr.PlSqlParser.Update_statementContext;
import page.foliage.jdbc.antlr.PlSqlParser.Where_clauseContext;
import page.foliage.jdbc.antlr.PlSqlParserBaseListener;
import page.foliage.jdbc.antlr.PlSqlUtils;
import page.foliage.jdbc.antlr.PlSqlUtils.TableField;
import page.foliage.jdbc.antlr.PlSqlUtils.TableFields;
import page.foliage.jdbc.antlr.PlSqlUtils.TableInfo;
import page.foliage.jdbc.interpret.Interpreter;

/**
 * 
 * 
 * @author deathknight0718@qq.com
 * @version 1.0.0
 */
public class OracleInterpreter implements Interpreter {

    // ------------------------------------------------------------------------

    public static final String DATABASE_NAME = "Oracle";

    // ------------------------------------------------------------------------

    private static final String SQL_PATTERN_REWRITE_QUERY = "SELECT * FROM ({0}) WHERE {1} >= ? AND {1} <= ?";

    private static final String SQL_PATTERN_REWRITE_RANGE = "SELECT MIN({1}), MAX({1}) FROM ({0})";

    // ------------------------------------------------------------------------

    private static final String LOGMINR_FUNCTION_FILE_HIT = StringUtils.join(new String[] { //
        " DECLARE                                                                                                      ", //
        "     L_FILE_NAME VARCHAR2(512) DEFAULT NULL;                                                                  ", //
        "     L_LOWER_SCN NUMBER        DEFAULT 0;                                                                     ", //
        "     L_UPPER_SCN NUMBER        DEFAULT 0;                                                                     ", //
        "     L_CURRENT_SCN NUMBER      DEFAULT 0;                                                                     ", //
        "     L_INPUT_SCN NUMBER        := ?;                                                                          ", //
        " BEGIN                                                                                                        ", //
        "     SELECT CURRENT_SCN INTO L_CURRENT_SCN FROM V$DATABASE;                                                   ", //
        "     IF L_CURRENT_SCN < L_INPUT_SCN THEN                                                                      ", //
        "         L_LOWER_SCN := L_CURRENT_SCN;                                                                        ", //
        "         L_UPPER_SCN := L_CURRENT_SCN;                                                                        ", //
        "     ELSE                                                                                                     ", //
        "         FOR REC IN (SELECT VLF.MEMBER AS VLF_MEMBER FROM V$LOGFILE VLF)                                      ", //
        "         LOOP                                                                                                 ", //
        "             -- DBMS_OUTPUT.PUT_LINE(REC.V_MEMBER);                                                           ", //
        "             DBMS_LOGMNR.ADD_LOGFILE(REC.VLF_MEMBER, DBMS_LOGMNR.ADDFILE);                                    ", //
        "         END LOOP;                                                                                            ", //
        "         FOR REC IN (SELECT FILENAME, LOW_SCN, NEXT_SCN FROM V$LOGMNR_LOGS ORDER BY LOW_SCN)                  ", //
        "         LOOP                                                                                                 ", //
        "             L_FILE_NAME := REC.FILENAME;                                                                     ", //
        "             L_LOWER_SCN := REC.LOW_SCN;                                                                      ", //
        "             L_UPPER_SCN := REC.NEXT_SCN;                                                                     ", //
        "             IF REC.LOW_SCN > L_INPUT_SCN OR (REC.LOW_SCN <= L_INPUT_SCN AND REC.NEXT_SCN > L_INPUT_SCN) THEN ", //
        "                 EXIT;                                                                                        ", //
        "             END IF;                                                                                          ", //
        "         END LOOP;                                                                                            ", //
        "         IF L_UPPER_SCN > L_CURRENT_SCN THEN                                                                  ", //
        "             L_UPPER_SCN := L_CURRENT_SCN;                                                                    ", //
        "         END IF;                                                                                              ", //
        "         FOR REC IN (SELECT VLF.MEMBER AS VLF_MEMBER FROM V$LOGFILE VLF)                                      ", //
        "         LOOP                                                                                                 ", //
        "             IF REC.VLF_MEMBER <> L_FILE_NAME THEN                                                            ", //
        "                 DBMS_LOGMNR.REMOVE_LOGFILE(REC.VLF_MEMBER);                                                  ", //
        "             END IF;                                                                                          ", //
        "         END LOOP;                                                                                            ", //
        "     END IF;                                                                                                  ", //
        "     ? := L_FILE_NAME;                                                                                        ", //
        "     ? := L_LOWER_SCN;                                                                                        ", //
        "     ? := L_UPPER_SCN;                                                                                        ", //
        " END;                                                                                                         ", //
    }, "\n");

    private static final String LOGMINR_FUNCTION_START = StringUtils.join(new String[] { //
        " BEGIN                                                     ", //
        "     DBMS_LOGMNR.START_LOGMNR(                             ", //
        "         STARTSCN => ${lower}, ENDSCN => ${upper},         ", //
        "         OPTIONS => DBMS_LOGMNR.SKIP_CORRUPTION +          ", //
        "                    DBMS_LOGMNR.NO_SQL_DELIMITER +         ", //
        "                    DBMS_LOGMNR.NO_ROWID_IN_STMT +         ", //
        "                    DBMS_LOGMNR.DICT_FROM_ONLINE_CATALOG + ", //
        "                    DBMS_LOGMNR.CONTINUOUS_MINE +          ", //
        "                    DBMS_LOGMNR.COMMITTED_DATA_ONLY +      ", //
        "                    DBMS_LOGMNR.STRING_LITERALS_IN_STMT);  ", //
        " END;                                                      ", //
    }, "\n");

    private static final String LOGMINR_FUNCTION_END = StringUtils.join(new String[] { //
        " BEGIN                         ", //
        "     DBMS_LOGMNR.END_LOGMNR(); ", //
        " EXCEPTION                     ", //
        "     WHEN OTHERS THEN NULL;    ", //
        " END;                          ", //
    }, "\n");

    private static final String LOGMINR_FUNCTION_QUERY = new SQLE() //
        .SELECT("SCN AS VLC_SCN") //
        // .SELECT("START_SCN AS VLC_START_SCN") //
        // .SELECT("COMMIT_SCN AS VLC_COMMIT_SCN") //
        // .SELECT("TIMESTAMP AS VLC_TIMESTAMP") //
        // .SELECT("START_TIMESTAMP AS VLC_START_TIMESTAMP") //
        // .SELECT("COMMIT_TIMESTAMP AS VLC_COMMIT_TIMESTAMP") //
        // .SELECT("XIDUSN AS VLC_XIDUSN") //
        // .SELECT("XIDSLT AS VLC_XIDSLT") //
        // .SELECT("XIDSQN AS VLC_XIDSQN") //
        // .SELECT("XID AS VLC_XID") //
        // .SELECT("PXIDUSN AS VLC_PXIDUSN") //
        // .SELECT("PXIDSLT AS VLC_PXIDSLT") //
        // .SELECT("PXIDSQN AS VLC_PXIDSQN") //
        // .SELECT("PXID AS VLC_PXID") //
        // .SELECT("TX_NAME AS VLC_TX_NAME") //
        // .SELECT("OPERATION AS VLC_OPERATION") //
        // .SELECT("OPERATION_CODE AS VLC_OPERATION_CODE") //
        // .SELECT("ROLLBACK AS VLC_ROLLBACK") //
        // .SELECT("SEG_OWNER AS VLC_SEG_OWNER") //
        // .SELECT("SEG_NAME AS VLC_SEG_NAME") //
        // .SELECT("TABLE_NAME AS VLC_TABLE_NAME") //
        // .SELECT("SEG_TYPE AS VLC_SEG_TYPE") //
        // .SELECT("SEG_TYPE_NAME AS VLC_SEG_TYPE_NAME") //
        // .SELECT("TABLE_SPACE AS VLC_TABLE_SPACE") //
        // .SELECT("ROW_ID AS VLC_ROW_ID") //
        // .SELECT("USERNAME AS VLC_USERNAME") //
        // .SELECT("OS_USERNAME AS VLC_OS_USERNAME") //
        // .SELECT("MACHINE_NAME AS VLC_MACHINE_NAME") //
        // .SELECT("AUDIT_SESSIONID AS VLC_AUDIT_SESSIONID") //
        // .SELECT("SESSION# AS VLC_SESSION") //
        // .SELECT("SERIAL# AS VLC_SERIAL") //
        // .SELECT("SESSION_INFO AS VLC_SESSION_INFO") //
        // .SELECT("THREAD# AS VLC_THREAD") //
        // .SELECT("SEQUENCE# AS VLC_SEQUENCE") //
        // .SELECT("RBASQN AS VLC_RBASQN") //
        // .SELECT("RBABLK AS VLC_RBABLK") //
        // .SELECT("RBABYTE AS VLC_RBABYTE") //
        // .SELECT("UBAFIL AS VLC_UBAFIL") //
        // .SELECT("UBABLK AS VLC_UBABLK") //
        // .SELECT("UBAREC AS VLC_UBAREC") //
        // .SELECT("UBASQN AS VLC_UBASQN") //
        // .SELECT("ABS_FILE# AS VLC_ABS_FILE") //
        // .SELECT("REL_FILE# AS VLC_REL_FILE") //
        // .SELECT("DATA_BLK# AS VLC_DATA_BLK") //
        // .SELECT("DATA_OBJ# AS VLC_DATA_OBJ") //
        // .SELECT("DATA_OBJV# AS VLC_DATA_OBJV") //
        // .SELECT("DATA_OBJD# AS VLC_DATA_OBJD") //
        .SELECT("SQL_REDO AS VLC_SQL_REDO") //
        // .SELECT("SQL_UNDO AS VLC_SQL_UNDO") //
        // .SELECT("RS_ID AS VLC_RS_ID") // 组合索引         
        // .SELECT("SSN AS VLC_SSN") // 组合索引             
        // .SELECT("CSF AS VLC_CSF") //
        // .SELECT("INFO AS VLC_INFO") //
        // .SELECT("STATUS AS VLC_STATUS") //
        // .SELECT("REDO_VALUE AS VLC_REDO_VALUE") //
        // .SELECT("UNDO_VALUE AS VLC_UNDO_VALUE") //
        // .SELECT("SAFE_RESUME_SCN AS VLC_SAFE_RESUME_SCN") //
        // .SELECT("CSCN AS VLC_CSCN") //
        // .SELECT("OBJECT_ID AS VLC_OBJECT_ID") //
        // .SELECT("EDITION_NAME AS VLC_EDITION_NAME") //
        // .SELECT("CLIENT_ID AS VLC_CLIENT_ID") //
        .FROM("V$LOGMNR_CONTENTS") //
        .WHERE("OPERATION_CODE IN (1,2,3)") //
        .AND() //
        .WHERE("SEG_OWNER NOT IN ('SYS', 'SYSTEM')") //
        .toString();

    private static Logger logger = LoggerFactory.getLogger(OracleInterpreter.class);

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

    public PreparedStatement logminerQuery(Connection connection) throws SQLException {
        logger.debug("Prepare Logminer SQL: {}", StringUtils.normalizeSpace(LOGMINR_FUNCTION_QUERY));
        return connection.prepareStatement(LOGMINR_FUNCTION_QUERY);
    }

    public LogminerBean logminerRedoPrase(long scn, String redo) throws SQLException {
        return LogminerBeanBuilder.with(scn, redo).build();
    }

    // ------------------------------------------------------------------------

    public CallableStatement logminerFileHit(Connection connection) throws SQLException {
        logger.debug("Prepare Logminer SQL: \n{}", LOGMINR_FUNCTION_FILE_HIT);
        return connection.prepareCall(LOGMINR_FUNCTION_FILE_HIT);
    }

    public LogminerFile logminerFileHitExecute(CallableStatement statement, long indexValue) throws SQLException {
        statement.registerOutParameter(2, Types.VARCHAR);
        statement.registerOutParameter(3, Types.NUMERIC);
        statement.registerOutParameter(4, Types.NUMERIC);
        logger.debug("Inject the logminer file: {}", indexValue);
        statement.setLong(1, indexValue);
        statement.execute();
        String name = statement.getString(2);
        long lower = statement.getLong(3);
        long upper = statement.getLong(4);
        logger.debug("Hit the logminer file: {}, {}, {}", name, lower, upper);
        return new LogminerFile(name, lower, upper);
    }

    public Statement logminerFileStart(Statement statement, LogminerFile file) throws SQLException {
        String executeSql = StringSubstitutor.replace(LOGMINR_FUNCTION_START, ImmutableMap.of("lower", file.lower(), "upper", file.upper()));
        logger.debug("Execute Logminer SQL: {}", StringUtils.normalizeSpace(executeSql));
        statement.execute(executeSql);
        return statement;
    }

    public Statement logminerFileEnd(Statement statement) throws SQLException {
        logger.debug("Execute Logminer SQL: {}", StringUtils.normalizeSpace(LOGMINR_FUNCTION_END));
        statement.execute(LOGMINR_FUNCTION_END);
        return statement;
    }

    // ------------------------------------------------------------------------

    public static class LogminerFile {

        private final String name;

        private final Range<Long> range;

        public LogminerFile(String name, long lower, long upper) {
            this.name = name;
            this.range = Range.closedOpen(lower, upper);
        }

        public boolean isEmpty() {
            return name == null;
        }

        public String getName() {
            return name;
        }

        public long lower() {
            return range.lowerEndpoint();
        }

        public long upper() {
            return range.upperEndpoint();
        }

    }

    // ------------------------------------------------------------------------

    private static class LogminerBeanBuilder extends PlSqlParserBaseListener {

        private LogminerBean bean;

        @Override
        public void enterInsert_statement(Insert_statementContext context) {
            bean.operation = LogminerOperation.INSERT;
            // System.err.println(AntlrUtils.toPrettyTree(context, Arrays.asList(PlSqlParser.ruleNames)));
        }

        @Override
        public void enterUpdate_statement(Update_statementContext context) {
            bean.operation = LogminerOperation.UPDATE;
            // System.err.println(AntlrUtils.toPrettyTree(context, Arrays.asList(PlSqlParser.ruleNames)));
        }

        @Override
        public void enterTableview_name(Tableview_nameContext context) {
            bean.tableInfo = PlSqlUtils.collectTableInfo(context);
        }

        @Override
        public void enterUpdate_set_clause(Update_set_clauseContext context) {
            bean.tableFields.putAll(PlSqlUtils.collectTableFields(context));
        }

        @Override
        public void enterWhere_clause(Where_clauseContext context) {
            bean.tableFields.putAll(PlSqlUtils.collectTableFields(context.expression().logical_expression()));
        }

        @Override
        public void enterSingle_table_insert(Single_table_insertContext context) {
            bean.tableFields.putAll(PlSqlUtils.collectTableFields(context.insert_into_clause().paren_column_list(), context.values_clause()));
        }

        public static LogminerBeanBuilder with(long scn, String redoSql) {
            LogminerBeanBuilder builder = new LogminerBeanBuilder();
            builder.bean = new LogminerBean(scn, redoSql);
            return builder;
        }

        public LogminerBean build() {
            CharStream stream = CharStreams.fromString(bean.redoSql);
            CaseChangingCharStream upperStream = new CaseChangingCharStream(stream, true);
            PlSqlLexer lexer = new PlSqlLexer(upperStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PlSqlParser parser = new PlSqlParser(tokens);
            parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
            ParseTreeWalker.DEFAULT.walk(this, parser.data_manipulation_language_statements());
            return bean;
        }

    }

    // ------------------------------------------------------------------------

    public static class LogminerBean implements Comparable<LogminerBean> {

        private final long scn;

        private LogminerOperation operation;

        private TableInfo tableInfo;

        private final String redoSql;

        private final TableFields tableFields = new TableFields();

        public LogminerBean(long scn, String redoSql) {
            this.scn = scn;
            this.redoSql = redoSql;
        }

        @Override
        public int compareTo(LogminerBean input) {
            return Math.toIntExact(scn - input.scn);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE) //
                .append(operation).append(tableInfo).append(tableFields) //
                .build();
        }

        public long getScn() {
            return scn;
        }

        public LogminerOperation getOperation() {
            return operation;
        }

        public TableInfo getTableInfo() {
            return tableInfo;
        }

        public TableFields getTableFields() {
            return tableFields;
        }

        public Map<String, Object> getTableFieldValues() {
            return Maps.transformValues(tableFields, TableField::getValue);
        }

        public String getRedoSql() {
            return redoSql;
        }

    }

    // ------------------------------------------------------------------------

    public static enum LogminerOperation {

        REDO, UPDATE, INSERT, DELETE;

    }

}
