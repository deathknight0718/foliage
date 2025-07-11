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
package page.foliage.ldap;

import static page.foliage.ldap.session.IdentitySessionFactory.openJdbcSession;
import static page.foliage.ldap.session.IdentitySessionFactory.openSession;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.collect.ImmutableList;
import page.foliage.guava.common.collect.Lists;
import page.foliage.ldap.session.IdentitySession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Domain {

    // ------------------------------------------------------------------------

    public static final Domain SYSTEM = new Domain(0L, "system", "平台管理中心");

    // ------------------------------------------------------------------------

    private final Long id;

    private final String identifier;

    private String displayName;

    // ------------------------------------------------------------------------

    public Domain(Long id, String identifier) {
        this.id = id;
        this.identifier = identifier;
    }

    public Domain(Long id, String identifier, String displayName) {
        this.id = id;
        this.identifier = identifier;
        this.displayName = displayName;
    }

    // ------------------------------------------------------------------------

    public static PaginList<Domain> list(QueryParams params) {
        try (IdentitySession session = openSession()) {
            return session.domainsSelectByParams(params);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Domain get(Long id) {
        try (IdentitySession session = openSession()) {
            return session.domainSelectById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Domain get(String identifier) {
        try (IdentitySession session = openSession()) {
            return session.domainSelectByIdentifier(identifier);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    public PaginList<Domain> members(QueryParams params) {
        try (IdentitySession session = openSession()) {
            return session.domainsRecursionByParamsAndRoot(params, this);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public PaginList<User> users(QueryParams params) {
        try (IdentitySession session = openSession()) {
            return session.usersSelectByParamsAndDomainId(params, id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public PaginList<Repository> repositories(QueryParams params) {
        try (IdentitySession session = openSession()) {
            return session.repositoriesSelectByParamsAndDomainId(params, id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public PaginList<Dashboard> dashboards(QueryParams params) {
        try (IdentitySession session = openJdbcSession()) {
            return session.dashboardsSelectByParamsAndDomainId(params, id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<Repository> repositoriesByAccessible() {
        ImmutableList.Builder<Repository> builder = ImmutableList.builder();
        try (IdentitySession session = openSession()) {
            builder.addAll(session.repositoriesSelectByParamsAndDomainId(QueryParams.ALL, id));
            builder.addAll(Lists.transform(session.contractsSelectByParamsAndDomainId(QueryParams.ALL, id), Contract::repository));
            return builder.build();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public PaginList<Contract> contracts(QueryParams params) {
        try (IdentitySession session = openSession()) {
            return session.contractsSelectByParamsAndDomainId(params, id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        Domain rhs = (Domain) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    // ------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
