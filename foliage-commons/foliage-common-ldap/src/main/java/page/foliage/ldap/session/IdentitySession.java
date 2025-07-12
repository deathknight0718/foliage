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

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.ldap.Dashboard;
import page.foliage.ldap.Dashboard.Builder;
import page.foliage.ldap.Domain;
import page.foliage.ldap.Role;
import page.foliage.ldap.User;

/**
 * 
 * @author deathknight0718@qq.com
 */
public interface IdentitySession extends AutoCloseable {

    PaginList<Domain> domainsSelectByParams(QueryParams params) throws Exception;

    PaginList<Domain> domainsRecursionByParamsAndRoot(QueryParams params, Domain root) throws Exception;

    Domain domainSelectById(Long id) throws Exception;

    Domain domainSelectByIdentifier(String identifier) throws Exception;

    PaginList<User> usersSelectByParamsAndDomainId(QueryParams params, Long domainId) throws Exception;

    User userSelectById(Long id) throws Exception;

    User userSelectByEmail(String email) throws Exception;

    User userSelectByName(String name) throws Exception;

    PaginList<Dashboard> dashboardsSelectByParamsAndDomainId(QueryParams params, Long domainId) throws Exception;

    Dashboard dashboardSelectById(Long id) throws Exception;

    Dashboard dashboardInsertOrUpdate(Builder builder) throws Exception;

    Long dashboardDeleteById(Long id) throws Exception;

    PaginList<Role> rolesSelectByParams(QueryParams params) throws Exception;

    PaginList<Role> rolesSelectByParamsAndUserId(QueryParams params, Long userId) throws Exception;

}
