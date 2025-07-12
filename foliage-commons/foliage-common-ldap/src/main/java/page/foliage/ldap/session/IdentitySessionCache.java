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

import java.util.UUID;

import page.foliage.common.collect.Identities;
import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.guava.common.cache.Cache;
import page.foliage.ldap.Dashboard;
import page.foliage.ldap.Domain;
import page.foliage.ldap.Role;
import page.foliage.ldap.User;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class IdentitySessionCache implements IdentitySession {

    // ------------------------------------------------------------------------

    private final IdentitySession delegate;

    private final Cache<UUID, Object> cache;

    // ------------------------------------------------------------------------

    public IdentitySessionCache(IdentitySession delegate, Cache<UUID, Object> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        delegate.close();
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public PaginList<Domain> domainsSelectByParams(QueryParams params) throws Exception {
        return (PaginList<Domain>) cache.get(Identities.uuid("domainsSelectByParams", params), () -> delegate.domainsSelectByParams(params));
    }

    @Override
    @SuppressWarnings("unchecked")
    public PaginList<Domain> domainsRecursionByParamsAndRoot(QueryParams params, Domain root) throws Exception {
        return (PaginList<Domain>) cache.get(Identities.uuid("domainsRecursionByParamsAndRoot", root), () -> delegate.domainsRecursionByParamsAndRoot(params, root));
    }

    @Override
    public Domain domainSelectById(Long id) throws Exception {
        return (Domain) cache.get(Identities.uuid("domainSelectById", id), () -> delegate.domainSelectById(id));
    }

    @Override
    public Domain domainSelectByIdentifier(String identifier) throws Exception {
        return (Domain) cache.get(Identities.uuid("domainSelectByIdentifier", identifier), () -> delegate.domainSelectByIdentifier(identifier));
    }

    @Override
    @SuppressWarnings("unchecked")
    public PaginList<User> usersSelectByParamsAndDomainId(QueryParams params, Long domainId) throws Exception {
        return (PaginList<User>) cache.get(Identities.uuid("usersSelectByParamsAndDomainId", params, domainId), () -> delegate.usersSelectByParamsAndDomainId(params, domainId));
    }

    @Override
    public User userSelectById(Long id) throws Exception {
        return (User) cache.get(Identities.uuid("userSelectById", id), () -> delegate.userSelectById(id));
    }

    @Override
    public User userSelectByEmail(String email) throws Exception {
        return (User) cache.get(Identities.uuid("userSelectByEmail", email), () -> delegate.userSelectByEmail(email));
    }

    @Override
    public User userSelectByName(String name) throws Exception {
        return (User) cache.get(Identities.uuid("userSelectByName", name), () -> delegate.userSelectByName(name));
    }

    @Override
    public PaginList<Dashboard> dashboardsSelectByParamsAndDomainId(QueryParams params, Long domainId) throws Exception {
        return delegate.dashboardsSelectByParamsAndDomainId(params, domainId);
    }

    @Override
    public Dashboard dashboardSelectById(Long id) throws Exception {
        return delegate.dashboardSelectById(id);
    }

    @Override
    public Dashboard dashboardInsertOrUpdate(Dashboard.Builder builder) throws Exception {
        return delegate.dashboardInsertOrUpdate(builder);
    }

    @Override
    public Long dashboardDeleteById(Long id) throws Exception {
        return delegate.dashboardDeleteById(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PaginList<Role> rolesSelectByParams(QueryParams params) throws Exception {
        return (PaginList<Role>) cache.get(Identities.uuid("rolesSelectByParams", params), () -> delegate.rolesSelectByParams(params));
    }

    @Override
    @SuppressWarnings("unchecked")
    public PaginList<Role> rolesSelectByParamsAndUserId(QueryParams params, Long userId) throws Exception {
        return (PaginList<Role>) cache.get(Identities.uuid("rolesSelectByParamsAndUserId", params, userId), () -> delegate.rolesSelectByParamsAndUserId(params, userId));
    }

}
