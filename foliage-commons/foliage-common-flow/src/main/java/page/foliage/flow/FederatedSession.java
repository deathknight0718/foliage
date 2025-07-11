/*
 * Copyright 2023 Foliage Develop Team.
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
package page.foliage.flow;

import static page.foliage.flow.FederatedEngine.KEYWORD_KEY;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.util.DateTimes;
import page.foliage.common.util.sql.SQLE;
import page.foliage.ldap.Domain;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.ImmutableList;
import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.guava.common.collect.Lists;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FederatedSession implements AutoCloseable {

    // ------------------------------------------------------------------------

    private final static Logger LOGGER = LoggerFactory.getLogger(FederatedSession.class);

    private final Connection connection;

    private final StringSubstitutor substitutor;

    // ------------------------------------------------------------------------

    public FederatedSession(Connection connection, String schema) {
        this.connection = connection;
        this.substitutor = new StringSubstitutor(ImmutableMap.of("schema", schema));
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    // ------------------------------------------------------------------------

    private Long nextval(String sequence) throws SQLException {
        String sql = new SQLE().SELECT(String.format("nextval('%s'::regclass)", sequence)).toTemplatedString(substitutor);
        try (Statement statement = connection.createStatement()) {
            LOGGER.debug("execute sql: {}", sql);
            try (ResultSet result = statement.executeQuery(sql)) {
                Preconditions.checkArgument(result.next());
                return result.getLong(1);
            }
        }
    }

    // ------------------------------------------------------------------------

    public PaginList<FormResource> resourcesSelectByParamsAndDomain(QueryParams params, Domain domain) throws SQLException {
        List<FormResource> list = Lists.newArrayList();
        SQLE sqle = new SQLE().SELECT("id_, name_, key_, tenant_id_, description_, update_time_").FROM("${schema}.fli_flow_form").WHERE("tenant_id_ = ?");
        if (params.containsKey(KEYWORD_KEY)) sqle.WHERE("key_ = ?");
        sqle.LIMIT(params.limit().toString()).OFFSET(params.offset().toString());
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            int index = 0;
            statement.setString(++index, domain.getIdentifier());
            if (params.containsKey(KEYWORD_KEY)) statement.setString(++index, params.get("key_"));
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    FormResource bean = new FormResource(result.getLong(1));
                    bean.setName(result.getString(2));
                    bean.setKey(result.getString(3));
                    bean.setTenantId(domain.getIdentifier());
                    bean.setDescription(result.getString(5));
                    bean.setUpdateTime(DateTimes.toLocalDateTime(result.getTimestamp(6)));
                    list.add(bean);
                }
            }
        }
        SQLE sqlc = new SQLE().SELECT("count(1)").FROM("${schema}.fli_flow_form").WHERE("tenant_id_ = ?");
        if (params.containsKey(KEYWORD_KEY)) sqle.WHERE("key_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqlc.toTemplatedString(substitutor))) {
            int index = 0;
            statement.setString(++index, domain.getIdentifier());
            if (params.containsKey(KEYWORD_KEY)) statement.setString(++index, params.get("key_"));
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                return PaginList.copyOf(list, result.getLong(1));
            }
        }
    }

    public FormResource resourceSelectById(Long id) throws SQLException {
        if (id == null || id == 0L) return null;
        SQLE sqle = new SQLE().SELECT("id_, name_, key_, tenant_id_, description_, update_time_").FROM("${schema}.fli_flow_form").WHERE("id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            statement.setLong(1, id);
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                FormResource bean = new FormResource(result.getLong(1));
                bean.setName(result.getString(2));
                bean.setKey(result.getString(3));
                bean.setTenantId(result.getString(4));
                bean.setDescription(result.getString(5));
                bean.setUpdateTime(DateTimes.toLocalDateTime(result.getTimestamp(6)));
                return bean;
            }
        }
    }

    public FormResource resourceDefaultByDomain(Domain domain) throws SQLException, IOException {
        SQLE sqle = new SQLE();
        sqle.SELECT("id_, name_, key_, tenant_id_, description_, update_time_").FROM("${schema}.fli_flow_form");
        sqle.WHERE("key_ = 'default'", "tenant_id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            statement.setString(1, domain.getIdentifier());
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    FormResource bean = new FormResource(result.getLong(1));
                    bean.setName(result.getString(2));
                    bean.setKey(result.getString(3));
                    bean.setTenantId(result.getString(4));
                    bean.setDescription(result.getString(5));
                    bean.setUpdateTime(DateTimes.toLocalDateTime(result.getTimestamp(6)));
                    return bean;
                }
            }
        }
        return FormResource.builder().domain(domain) //
            .defaultKey().name("任务审核表单") //
            .resource("rjsf/202401091404.rjsf") //
            .build(this);
    }

    public FormResource resourceSelectByKeyAndDomain(String key, Domain domain) throws SQLException, IOException {
        if (FormResource.KEY_DEFAULT.equals(key)) return resourceDefaultByDomain(domain);
        SQLE sqle = new SQLE();
        sqle.SELECT("f1.id_, f1.name_, f1.key_, f1.tenant_id_, f1.description_, f1.update_time_").FROM("${schema}.fli_flow_form f1");
        sqle.LEFT_OUTER_JOIN("${schema}.fli_flow_form f2 ON f1.key_ = f2.key_ AND f1.tenant_id_ = f2.tenant_id_ AND f1.update_time_ < f2.update_time_");
        sqle.WHERE("f1.key_ = ?", "f1.tenant_id_ = ?", "f2.id_ is NULL");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            statement.setString(1, key);
            statement.setString(2, domain.getIdentifier());
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                FormResource bean = new FormResource(result.getLong(1));
                bean.setName(result.getString(2));
                bean.setKey(result.getString(3));
                bean.setTenantId(result.getString(4));
                bean.setDescription(result.getString(5));
                bean.setUpdateTime(DateTimes.toLocalDateTime(result.getTimestamp(6)));
                return bean;
            }
        }
    }

    public FormResource resourceInsertOrUpdateByBuilder(FormResource.Builder builder) throws SQLException, IOException {
        Long id = builder.id != null ? builder.id : nextval("${schema}.fli_flow_form_id__seq");
        SQLE sqle = new SQLE();
        sqle.INSERT_INTO("${schema}.fli_flow_form");
        sqle.VALUES("id_, name_, key_, tenant_id_, description_, update_time_, payload_", "?, ?, ?, ?, ?, now(), ?::bytea");
        sqle.ON_CONFLICT("id_");
        sqle.DO_UPDATE_SET("name_ = ?", "description_ = ?", "update_time_ = now()", "payload_ = ?::bytea");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            statement.setLong(1, id);
            statement.setString(2, builder.name);
            statement.setString(3, builder.key);
            statement.setString(4, builder.tenantId);
            statement.setString(5, builder.description);
            statement.setBytes(6, builder.resource.read());
            statement.setString(7, builder.name);
            statement.setString(8, builder.description);
            statement.setBytes(9, builder.resource.read());
            LOGGER.debug("execute sql: {}", statement);
            int callback = statement.executeUpdate();
            if (callback != 1) LOGGER.warn("Warning! nothing happened when executing sql: {}", statement);
            FormResource bean = new FormResource(id);
            bean.setName(builder.name);
            bean.setKey(builder.key);
            bean.setTenantId(builder.tenantId);
            bean.setDescription(builder.description);
            bean.setUpdateTime(LocalDateTime.now());
            return bean;
        }
    }

    public Long resourceDeleteById(Long id) throws SQLException {
        SQLE sqle = new SQLE().DELETE_FROM("${schema}.fli_flow_form").WHERE("id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            statement.setLong(1, id);
            LOGGER.debug("execute sql: {}", statement);
            int callback = statement.executeUpdate();
            if (callback != 1) LOGGER.warn("Warning! nothing happened when executing sql: {}", statement);
            return id;
        }
    }

    public InputStream resourcePayloadAsStreamById(Long id) throws SQLException {
        if (id == null) return null;
        SQLE sqle = new SQLE().SELECT("payload_::bytea").FROM("${schema}.fli_flow_form").WHERE("id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            statement.setLong(1, id);
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                return result.getBinaryStream(1);
            }
        }
    }

    // ------------------------------------------------------------------------

    public List<FormPayloadReference> referencesSelectByProcessId(String processId) throws SQLException, IOException {
        ImmutableList.Builder<FormPayloadReference> builder = ImmutableList.builder();
        SQLE sqle = new SQLE().SELECT("id_, key_, process_id_, execution_id_, update_time_").FROM("${schema}.fli_flow_form_ref").WHERE("process_id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            statement.setString(1, processId);
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    FormPayloadReference bean = new FormPayloadReference(result.getLong(1));
                    bean.setKey(result.getString(2));
                    bean.setProcessId(result.getString(3));
                    bean.setExecutionId(result.getString(4));
                    bean.setUpdateTime(DateTimes.toLocalDateTime(result.getTimestamp(5)));
                    builder.add(bean);
                }
                return builder.build();
            }
        }
    }

    public FormPayloadReference referenceSelectById(Long id) throws SQLException, IOException {
        if (id == null || id == 0L) return null;
        SQLE sqle = new SQLE().SELECT("id_, key_, process_id_, execution_id_, update_time_").FROM("${schema}.fli_flow_form_ref").WHERE("id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            statement.setLong(1, id);
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                FormPayloadReference bean = new FormPayloadReference(result.getLong(1));
                bean.setKey(result.getString(2));
                bean.setProcessId(result.getString(3));
                bean.setExecutionId(result.getString(4));
                bean.setUpdateTime(DateTimes.toLocalDateTime(result.getTimestamp(5)));
                return bean;
            }
        }
    }

    public FormPayloadReference referenceInsertByBuilder(FormPayloadReference.Builder builder) throws SQLException, JsonProcessingException {
        Long id = nextval("${schema}.fli_flow_form_ref_id__seq");
        SQLE sqle = new SQLE();
        sqle.INSERT_INTO("${schema}.fli_flow_form_ref");
        sqle.VALUES("id_, key_, process_id_, execution_id_, update_time_, payload_", "?, ?, ?, ?, now(), ?::jsonb");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            statement.setLong(1, id);
            statement.setString(2, builder.key);
            statement.setString(3, builder.processId);
            statement.setString(4, builder.executionId);
            statement.setString(5, builder.source.toString());
            LOGGER.debug("execute sql: {}", statement);
            int callback = statement.executeUpdate();
            if (callback != 1) LOGGER.warn("Warning! nothing happened when executing sql: {}", statement);
            FormPayloadReference bean = new FormPayloadReference(id);
            bean.setKey(builder.key);
            bean.setProcessId(builder.processId);
            bean.setExecutionId(builder.executionId);
            bean.setUpdateTime(LocalDateTime.now());
            return bean;
        }
    }

    public FormPayload payloadSelectById(Long id) throws SQLException {
        if (id == null || id == 0L) return null;
        SQLE sqle = new SQLE().SELECT("payload_").FROM("${schema}.fli_flow_form_ref").WHERE("id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toTemplatedString(substitutor))) {
            statement.setLong(1, id);
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                return FormPayload.of(result.getString(1));
            }
        }
    }

}
