/*
 * Copyright 2024 Foliage Develop Team.
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
package page.foliage.ldap;

import static page.foliage.ldap.session.IdentitySessionFactory.openJdbcSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import page.foliage.common.collect.QueryParams;
import page.foliage.common.util.JsonNodes;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.ImmutableList;
import page.foliage.guava.common.collect.ImmutableSet;
import page.foliage.ldap.session.IdentitySession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class StatelessAccess implements Access {

    // ------------------------------------------------------------------------

    private static final String PATH_CONST = "const";

    private static final String PATH_TITLE = "title";

    // ------------------------------------------------------------------------

    private User user;

    private Domain domain;

    private List<Domain> domains;

    private Set<Repository> accessibleRepositories;

    private Set<Role> roles;

    private List<Dashboard> dashboards;

    // ------------------------------------------------------------------------

    public StatelessAccess() {}

    // ------------------------------------------------------------------------

    public static StatelessAccess fromEmail(String email) {
        try (IdentitySession session = openJdbcSession()) {
            StatelessAccess bean = new StatelessAccess();
            bean.user = session.userSelectByEmail(email);
            bean.domain = session.domainSelectById(bean.user.getDomainId());
            bean.roles = ImmutableSet.copyOf(session.rolesSelectByParamsAndUserId(QueryParams.ALL, bean.user.getId()));
            ImmutableSet.Builder<Repository> repositories = ImmutableSet.builder();
            repositories.addAll(session.repositoriesSelectByParamsAndDomainId(QueryParams.ALL, bean.user.getDomainId()));
            for (Contract contract : session.contractsSelectByParamsAndDomainId(QueryParams.ALL, bean.user.getDomainId())) {
                repositories.add(contract.repository());
            }
            bean.accessibleRepositories = repositories.build();
            if (bean.user.isMemberOf(Role.SYSTEM_ADMIN)) bean.domains = ImmutableList.copyOf(session.domainsSelectByParams(QueryParams.ALL));
            else bean.domains = ImmutableList.of(bean.domain);
            bean.dashboards = session.dashboardsSelectByParamsAndDomainId(QueryParams.ALL, bean.user.getDomainId());
            return bean;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static StatelessAccess create(Domain domain) {
        StatelessAccess bean = new StatelessAccess();
        bean.domain = domain;
        bean.accessibleRepositories = ImmutableSet.of();
        bean.roles = ImmutableSet.of();
        return bean;
    }

    // ------------------------------------------------------------------------

    public LocalDate currentDate() {
        return LocalDate.now();
    }

    public LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }

    public JsonNode domains() {
        Preconditions.checkArgument(user.isMemberOf(Role.DOMAIN_ADMIN, Role.SYSTEM_ADMIN));
        ArrayNode result = JsonNodes.createArrayNode();
        for (Domain item : Domain.list(QueryParams.ALL)) {
            ObjectNode node = JsonNodes.createObjectNode();
            node.set(PATH_CONST, LongNode.valueOf(item.getId()));
            node.set(PATH_TITLE, TextNode.valueOf(item.getDisplayName()));
            result.add(node);
        }
        return result;
    }

    public JsonNode repositories() {
        Preconditions.checkArgument(user.isMemberOf(Role.DOMAIN_ADMIN, Role.SYSTEM_ADMIN));
        ArrayNode result = JsonNodes.createArrayNode();
        for (Repository item : domain.repositories(QueryParams.ALL)) {
            ObjectNode node = JsonNodes.createObjectNode();
            node.set(PATH_CONST, LongNode.valueOf(item.getId()));
            node.set(PATH_TITLE, TextNode.valueOf(item.getDisplayName()));
            result.add(node);
        }
        return result;
    }

    // ------------------------------------------------------------------------

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public List<Domain> getDomains() {
        return domains;
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    public Set<Repository> getAccessibleRepositories() {
        return accessibleRepositories;
    }

    public List<Dashboard> getDashboards() {
        return dashboards;
    }

}
