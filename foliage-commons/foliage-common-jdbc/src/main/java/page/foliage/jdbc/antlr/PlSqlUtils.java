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
package page.foliage.jdbc.antlr;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import page.foliage.jdbc.antlr.PlSqlParser;
import page.foliage.jdbc.antlr.PlSqlParser.ArgumentContext;
import page.foliage.jdbc.antlr.PlSqlParser.AtomContext;
import page.foliage.jdbc.antlr.PlSqlParser.Column_based_update_set_clauseContext;
import page.foliage.jdbc.antlr.PlSqlParser.Column_nameContext;
import page.foliage.jdbc.antlr.PlSqlParser.ConstantContext;
import page.foliage.jdbc.antlr.PlSqlParser.ExpressionContext;
import page.foliage.jdbc.antlr.PlSqlParser.General_element_partContext;
import page.foliage.jdbc.antlr.PlSqlParser.Id_expressionContext;
import page.foliage.jdbc.antlr.PlSqlParser.Logical_expressionContext;
import page.foliage.jdbc.antlr.PlSqlParser.Multiset_expressionContext;
import page.foliage.jdbc.antlr.PlSqlParser.Non_reserved_keywords_pre12cContext;
import page.foliage.jdbc.antlr.PlSqlParser.Paren_column_listContext;
import page.foliage.jdbc.antlr.PlSqlParser.Relational_expressionContext;
import page.foliage.jdbc.antlr.PlSqlParser.Tableview_nameContext;
import page.foliage.jdbc.antlr.PlSqlParser.Update_set_clauseContext;
import page.foliage.jdbc.antlr.PlSqlParser.Values_clauseContext;

import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.collect.ForwardingMap;
import page.foliage.guava.common.collect.Maps;

/**
 * 
 * 
 * @author deathknight0718@qq.com
 * @version 1.0.0
 */
public class PlSqlUtils {

    private static final DateTimeFormatter FORMATTER_OF_TIMESTAMP = DateTimeFormatter.ofPattern("'' yyyy-MM-dd HH:mm:ss''").withZone(ZoneId.systemDefault());

    private static final List<String> RULE_NAMES = Arrays.asList(PlSqlParser.ruleNames);

    private PlSqlUtils() {};

    // ------------------------------------------------------------------------

    public static class TableInfo {

        private final String name, schema;

        public TableInfo(String name, String schema) {
            this.name = name;
            this.schema = schema;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) { return false; }
            if (obj == this) { return true; }
            if (obj.getClass() != getClass()) { return false; }
            TableInfo rhs = (TableInfo) obj;
            return new EqualsBuilder().append(name, rhs.name).append(schema, rhs.schema).isEquals();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, schema);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("name", name).append("schema", schema).build();
        }

        public String getName() {
            return name;
        }

        public String getFullName() {
            return schema == null ? name : schema + "." + name;
        }

        public String getSchema() {
            return schema;
        }

    }

    // ------------------------------------------------------------------------

    public static class TableFields extends ForwardingMap<String, TableField> {

        private final Map<String, TableField> delegate = Maps.newLinkedHashMap();

        @Override
        protected Map<String, TableField> delegate() {
            return delegate;
        }

        public static TableFields of(TableField field) {
            TableFields fields = new TableFields();
            fields.put(field.name, field);
            return fields;
        }

        public static TableFields of(String name, JDBCType type, Object value) {
            TableFields fields = new TableFields();
            fields.put(name, new TableField(name, type, value));
            return fields;
        }

    }

    // ------------------------------------------------------------------------

    public static class TableField {

        private final String name;

        private JDBCType type;

        private Object value;

        public TableField(String name, JDBCType type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) { return false; }
            if (obj == this) { return true; }
            if (obj.getClass() != getClass()) { return false; }
            TableField rhs = (TableField) obj;
            return new EqualsBuilder().append(name, rhs.name).append(type, rhs.type).append(value, rhs.value).isEquals();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, type, value);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("name", name).append("type", type).append("value", value).build();
        }

        public String getName() {
            return name;
        }

        public JDBCType getType() {
            return type;
        }

        public void setType(JDBCType type) {
            this.type = type;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

    }

    // ------------------------------------------------------------------------

    public static TableInfo collectTableInfo(Tableview_nameContext context) {
        return new TableInfo(extractStringFromExpressionOfId(context.id_expression()), extractStringFromExpressionOfId(context.identifier().id_expression()));
    }

    public static TableFields collectTableFields(Update_set_clauseContext context) {
        TableFields fields = new TableFields();
        for (Column_based_update_set_clauseContext columnContext : context.column_based_update_set_clause()) {
            String name = extractStringFromExpressionOfId(findFirstRuleNode(columnContext.column_name(), PlSqlParser.RULE_id_expression));
            TableField field = extractAtomTableField(name, findFirstRuleNode(columnContext.expression(), PlSqlParser.RULE_atom));
            fields.put(name, field);
        }
        return fields;
    }

    public static TableFields collectTableFields(Logical_expressionContext context) {
        if (context.OR() != null) throw new IllegalArgumentException("Error! cannot parse 'OR' rule: " + context.toStringTree(RULE_NAMES));
        if (context.multiset_expression() != null) {
            if (!context.IS().isEmpty() && !context.NULL_().isEmpty()) return TableFields.of(extractStringFromExpressionOfId(findFirstRuleNode(context.multiset_expression(), PlSqlParser.RULE_id_expression)), JDBCType.NULL, null);
            return collectTableFields(context.multiset_expression());
        } else {
            TableFields result = new TableFields();
            for (Logical_expressionContext child : context.logical_expression()) {
                result.putAll(collectTableFields(child));
            }
            return result;
        }
    }

    public static TableFields collectTableFields(Multiset_expressionContext context) {
        if (context.relational_expression() == null) throw new IllegalArgumentException("Error! child rule must be 'relational_expression': " + context.toStringTree(RULE_NAMES));
        return collectTableFields(context.relational_expression());
    }

    public static TableFields collectTableFields(Relational_expressionContext context) {
        if (context.relational_operator() != null) { // 如果为粒子性表达式组合
            if (context.relational_operator().EQUALS_OP() == null) throw new IllegalArgumentException("Error! operator must be 'EQUALS_OP' rule: " + context.toStringTree(RULE_NAMES));
            String name = extractStringFromExpressionOfId(findFirstRuleNode(context.relational_expression(0), PlSqlParser.RULE_id_expression));
            TableField field = extractAtomTableField(name, findFirstRuleNode(context.relational_expression(1), PlSqlParser.RULE_atom));
            return TableFields.of(field);
        } else throw new IllegalArgumentException("Error! child rule must be has 'relational_operator' or 'is null': " + context.toStringTree(RULE_NAMES));
    }

    public static TableFields collectTableFields(Paren_column_listContext parenColumnListContext, Values_clauseContext valuesClauseContext) {
        List<Column_nameContext> columnNameContexts = parenColumnListContext.column_list().column_name();
        List<ExpressionContext> expressionContexts = valuesClauseContext.expressions().expression();
        TableFields fields = new TableFields();
        for (int i = 0; i < columnNameContexts.size(); i++) {
            String name = extractStringFromExpressionOfId(findFirstRuleNode(columnNameContexts.get(i), PlSqlParser.RULE_id_expression));
            TableField field = extractAtomTableField(name, findFirstRuleNode(expressionContexts.get(i), PlSqlParser.RULE_atom));
            fields.put(name, field);
        }
        return fields;
    }

    // ------------------------------------------------------------------------

    private static String extractStringFromExpressionOfId(Id_expressionContext context) {
        if (context.DELIMITED_ID() != null) return StringUtils.substringBetween(context.DELIMITED_ID().getText(), "\"", "\"");
        if (context.regular_id() != null) return null;
        throw new IllegalArgumentException("Error! cannot parse id expression rule: " + context);
    }

    private static TableField extractAtomTableField(String name, AtomContext context) {
        if (context.constant() != null) {
            ConstantContext constant = context.constant();
            if (constant.NULL_() != null) return new TableField(name, JDBCType.NULL, null);
            if (constant.numeric() != null) return new TableField(name, JDBCType.NUMERIC, new BigDecimal(constant.numeric().getText()).doubleValue());
            if (constant.TIMESTAMP() != null) {
                LocalDateTime localDateTime = LocalDateTime.parse(constant.quoted_string().iterator().next().getText(), FORMATTER_OF_TIMESTAMP);
                return new TableField(name, JDBCType.TIMESTAMP, Timestamp.valueOf(localDateTime));
            }
            if (!constant.quoted_string().isEmpty()) return new TableField(name, JDBCType.VARCHAR, StringUtils.substringBetween(constant.quoted_string().iterator().next().getText(), "'", "'"));
        }
        if (context.general_element() != null) {
            General_element_partContext part = context.general_element().general_element_part().iterator().next();
            if (!part.id_expression().isEmpty()) {
                Id_expressionContext expression = part.id_expression().iterator().next();
                if (expression.regular_id() != null) {
                    Non_reserved_keywords_pre12cContext keywords = expression.regular_id().non_reserved_keywords_pre12c();
                    if (keywords != null && keywords.HEXTORAW() != null) { // HEXTORAW 函数处理
                        if (part.function_argument() == null) throw new IllegalArgumentException("Error! function argument rule must be not null: " + part.toStringTree(RULE_NAMES));
                        if (part.function_argument().argument().size() != 1) throw new IllegalArgumentException("Error! function argument rule must be 1: " + part.toStringTree(RULE_NAMES));
                        ArgumentContext argument = part.function_argument().argument().iterator().next();
                        TableField hexValue = extractAtomTableField(name, findFirstRuleNode(argument, PlSqlParser.RULE_atom));
                        if (JDBCType.VARCHAR != hexValue.type) throw new IllegalArgumentException("Error! value type must be string: " + hexValue);
                        return new TableField(name, JDBCType.BLOB, applyHexToRaw(String.valueOf(hexValue.value)));
                    }
                }
                return new TableField(name, JDBCType.VARCHAR, extractStringFromExpressionOfId(expression));
            }
        }
        throw new IllegalArgumentException("Error! cannot parse atom rule: " + context.toStringTree(RULE_NAMES));
    }

    // ------------------------------------------------------------------------

    /**
     * 深度优先算法遍历语法树规则集，获取首个匹配的规则
     */
    @SuppressWarnings("unchecked")
    private static <T extends ParseTree> T findFirstRuleNode(ParseTree node, int ruleIndex) {
        if (node instanceof ParserRuleContext) {
            ParserRuleContext context = (ParserRuleContext) node;
            if (context.getRuleIndex() == ruleIndex) return (T) node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            T rnode = findFirstRuleNode(node.getChild(i), ruleIndex);
            if (rnode != null) return rnode;
        }
        return null;
    }

    /**
     * 针对二进制数据值的转换
     */
    private static byte[] applyHexToRaw(String input) {
        int length = input.length();
        byte[] raw = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            raw[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4) + Character.digit(input.charAt(i + 1), 16));
        }
        return raw;
    }

}
