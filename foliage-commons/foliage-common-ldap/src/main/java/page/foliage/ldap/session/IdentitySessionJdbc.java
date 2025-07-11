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
import page.foliage.ldap.Contract;
import page.foliage.ldap.Dashboard;
import page.foliage.ldap.Domain;
import page.foliage.ldap.Repository;
import page.foliage.ldap.Role;
import page.foliage.ldap.User;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class IdentitySessionJdbc implements IdentitySession {

    // ------------------------------------------------------------------------

    private final static Logger LOGGER = LoggerFactory.getLogger(IdentitySessionJdbc.class);

    private final IdentitySession delegate;

    private final Connection connection;

    // ------------------------------------------------------------------------

    public IdentitySessionJdbc(IdentitySession delegate, Connection connection) {
        this.delegate = delegate;
        this.connection = connection;
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        connection.close();
        delegate.close();
    }

    // ------------------------------------------------------------------------

    @Override
    public PaginList<Domain> domainsSelectByParams(QueryParams params) throws Exception {
        return delegate.domainsSelectByParams(params);
    }

    @Override
    public PaginList<Domain> domainsRecursionByParamsAndRoot(QueryParams params, Domain root) throws Exception {
        return delegate.domainsRecursionByParamsAndRoot(params, root);
    }

    @Override
    public Domain domainSelectById(Long id) throws Exception {
        return delegate.domainSelectById(id);
    }

    @Override
    public Domain domainSelectByIdentifier(String identifier) throws Exception {
        return delegate.domainSelectByIdentifier(identifier);
    }

    // ------------------------------------------------------------------------

    @Override
    public PaginList<User> usersSelectByParamsAndDomainId(QueryParams params, Long domainId) throws Exception {
        return delegate.usersSelectByParamsAndDomainId(params, domainId);
    }

    @Override
    public User userSelectById(Long id) throws Exception {
        return delegate.userSelectById(id);
    }

    @Override
    public User userSelectByEmail(String email) throws Exception {
        return delegate.userSelectByEmail(email);
    }

    @Override
    public User userSelectByName(String name) throws Exception {
        return delegate.userSelectByName(name);
    }

    // ------------------------------------------------------------------------

    @Override
    public PaginList<Repository> repositoriesSelectByParamsAndDomainId(QueryParams params, Long domainId) throws Exception {
        return delegate.repositoriesSelectByParamsAndDomainId(params, domainId);
    }

    @Override
    public Repository repositorySelectById(Long id) throws Exception {
        return delegate.repositorySelectById(id);
    }

    // ------------------------------------------------------------------------

    @Override
    public PaginList<Contract> contractsSelectByParamsAndRepositoryId(QueryParams params, Long repositoryId) throws Exception {
        return delegate.contractsSelectByParamsAndRepositoryId(params, repositoryId);
    }

    @Override
    public PaginList<Contract> contractsSelectByParamsAndDomainId(QueryParams params, Long domainId) throws Exception {
        return delegate.contractsSelectByParamsAndDomainId(params, domainId);
    }

    @Override
    public Contract contractSelectById(Long id) throws Exception {
        return delegate.contractSelectById(id);
    }

    @Override
    public Contract contractInsert(Contract.Builder builder) throws Exception {
        return delegate.contractInsert(builder);
    }

    @Override
    public void contractDeleteById(Long id) throws Exception {
        delegate.contractDeleteById(id);
    }

    // ------------------------------------------------------------------------

    @Override
    public PaginList<Dashboard> dashboardsSelectByParamsAndDomainId(QueryParams params, Long domainId) throws Exception {
        ImmutableList.Builder<Dashboard> builder = ImmutableList.builder();
        SQLE sqle = new SQLE().SELECT("id_, name_, domain_id_, dashboard_id_, dashboard_token_").FROM("public.fli_core_dashboard").WHERE("domain_id_ = ?");
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
        SQLE sqlc = new SQLE().SELECT("count(1)").FROM("public.fli_core_dashboard").WHERE("domain_id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqlc.toNormalizeString())) {
            statement.setLong(1, domainId);
            LOGGER.debug("execute sql: {}", statement);
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                return PaginList.copyOf(builder.build(), result.getLong(1));
            }
        }
    }

    @Override
    public Dashboard dashboardSelectById(Long id) throws Exception {
        SQLE sqle = new SQLE().SELECT("id_, name_, domain_id_, dashboard_id_, dashboard_token_").FROM("public.fli_core_dashboard").WHERE("id_ = ?");
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

    @Override
    public Dashboard dashboardInsertOrUpdate(Dashboard.Builder builder) throws Exception {
        Long id = builder.getId() != null ? builder.getId() : Identities.snowflake();
        SQLE sqle1 = new SQLE();
        sqle1.INSERT_INTO("public.fli_core_dashboard");
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

    @Override
    public Long dashboardDeleteById(Long id) throws Exception {
        SQLE sqle = new SQLE().DELETE_FROM("public.fli_core_dashboard").WHERE("id_ = ?");
        try (PreparedStatement statement = connection.prepareStatement(sqle.toNormalizeString())) {
            statement.setLong(1, id);
            LOGGER.debug("execute sql: {}", statement);
            int callback = statement.executeUpdate();
            if (callback != 1) LOGGER.warn("Warning! nothing happened when executing sql: {}", statement);
            return id;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public PaginList<Role> rolesSelectByParams(QueryParams params) throws Exception {
        return delegate.rolesSelectByParams(params);
    }

    @Override
    public PaginList<Role> rolesSelectByParamsAndUserId(QueryParams params, Long userId) throws Exception {
        return delegate.rolesSelectByParamsAndUserId(params, userId);
    }

}
