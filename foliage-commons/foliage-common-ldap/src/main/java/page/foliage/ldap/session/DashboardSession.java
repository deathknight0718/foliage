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
package page.foliage.ldap.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import page.foliage.common.collect.Identities;
import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.util.sql.SQLE;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.ImmutableList;
import page.foliage.ldap.Dashboard;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class DashboardSession implements AutoCloseable {

    // ------------------------------------------------------------------------

    private final static Logger LOGGER = LoggerFactory.getLogger(DashboardSession.class);

    private final Connection connection;

    // ------------------------------------------------------------------------

    public DashboardSession(Connection connection) {
        this.connection = connection;
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        connection.close();
    }

    // ------------------------------------------------------------------------

    public PaginList<Dashboard> dashboardsSelectByParamsAndDomainId(QueryParams params, Long domainId) throws Exception {
        ImmutableList.Builder<Dashboard> builder = ImmutableList.builder();
        SQLE sqle = new SQLE().SELECT("id_, name_, domain_id_, dashboard_id_, dashboard_token_").FROM("ldap.fli_ldap_dashboard").WHERE("domain_id_ = ?");
        sqle.LIMIT(params.limit().toString()).OFFSET(params.offset().toString());
        try (PreparedStatement statement = connection.prepareStatement(sqle.toNormalizeString())) {
            statement.setLong(1, domainId);
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Dashboard bean = new Dashboard(result.getLong(1));
                    bean.setName(result.getString(2));
                    bean.setDomainId(result.getLong(3));
                    bean.setReferenceId(result.getString(4));
                    bean.setToken(result.getString(5));
                    builder.add(bean);
                }
            }
        }
        SQLE sqlc = new SQLE().SELECT("count(1)").FROM("ldap.fli_ldap_dashboard").WHERE("domain_id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqlc.toNormalizeString())) {
            statement.setLong(1, domainId);
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                return PaginList.copyOf(builder.build(), result.getLong(1));
            }
        }
    }

    public Dashboard dashboardSelectById(Long id) throws Exception {
        SQLE sqle = new SQLE().SELECT("id_, name_, domain_id_, dashboard_id_, dashboard_token_").FROM("ldap.fli_ldap_dashboard").WHERE("id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toNormalizeString())) {
            statement.setLong(1, id);
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                Dashboard bean = new Dashboard(result.getLong(1));
                bean.setName(result.getString(2));
                bean.setDomainId(result.getLong(3));
                bean.setReferenceId(result.getString(4));
                bean.setToken(result.getString(5));
                return bean;
            }
        }
    }

    public Dashboard dashboardInsertOrUpdate(Dashboard.Builder builder) throws Exception {
        Long id = builder.getId() != null ? builder.getId() : Identities.snowflake();
        SQLE sqle1 = new SQLE();
        sqle1.INSERT_INTO("ldap.fli_ldap_dashboard");
        sqle1.VALUES("id_, name_, domain_id_, dashboard_id_, dashboard_token_, update_time_", "?, ?, ?, ?, ?, now()");
        sqle1.ON_CONFLICT("id_");
        sqle1.DO_UPDATE_SET("name_ = ?", "dashboard_id_ = ?", "dashboard_token_ = ?", "update_time_ = now()");
        try (PreparedStatement statement = connection.prepareStatement(sqle1.toNormalizeString())) {
            statement.setLong(1, id);
            statement.setString(2, builder.getName());
            statement.setLong(3, builder.getAccess().getDomain().getId());
            statement.setString(4, builder.getReferenceId());
            statement.setString(5, builder.getToken());
            statement.setString(6, builder.getName());
            statement.setString(7, builder.getReferenceId());
            statement.setString(8, builder.getToken());
            LOGGER.warn("execute sql: {}", statement);
            int callback = statement.executeUpdate();
            if (callback != 1) LOGGER.warn("Warning! nothing happened when executing sql: {}", statement);
            Dashboard bean = new Dashboard(id);
            bean.setDomainId(builder.getAccess().getDomain().getId());
            bean.setName(builder.getName());
            bean.setReferenceId(builder.getReferenceId());
            bean.setToken(builder.getToken());
            return bean;
        }
    }

    public Long dashboardDeleteById(Long id) throws Exception {
        SQLE sqle = new SQLE().DELETE_FROM("ldap.fli_ldap_dashboard").WHERE("id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toNormalizeString())) {
            statement.setLong(1, id);
            LOGGER.debug("execute sql: {}", statement);
            int callback = statement.executeUpdate();
            if (callback != 1) LOGGER.warn("Warning! nothing happened when executing sql: {}", statement);
            return id;
        }
    }

}
