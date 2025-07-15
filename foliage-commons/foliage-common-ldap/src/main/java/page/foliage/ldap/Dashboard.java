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

import static page.foliage.ldap.session.DashboardSessionFactory.openSession;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.jackson.Hex36Serializer;
import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.ldap.session.DashboardSession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Dashboard {

    // ------------------------------------------------------------------------

    private final Long id;

    private Long domainId;

    private String referenceId, name, token;

    // ------------------------------------------------------------------------

    public Dashboard(Long id) {
        this.id = id;
    }

    // ------------------------------------------------------------------------

    public static Builder builder(Access access) {
        return new Dashboard.Builder(access);
    }

    // ------------------------------------------------------------------------

    public static Dashboard create(Long id) {
        return new Dashboard(id);
    }

    public static Dashboard get(Long id) {
        try (DashboardSession session = openSession()) {
            return session.dashboardSelectById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static PaginList<Dashboard> list(QueryParams params, Domain domain) {
        try (DashboardSession session = openSession()) {
            return session.dashboardsSelectByParamsAndDomainId(params, domain.getId());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    public Domain domain() {
        return Domain.get(domainId);
    }

    public void remove() {
        try (DashboardSession session = openSession()) {
            session.dashboardDeleteById(id);
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
        Dashboard rhs = (Dashboard) obj;
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

    @JsonSerialize(using = Hex36Serializer.class)
    public Long getId() {
        return id;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private final Access access;

        private Long id;

        private String referenceId, name, token;

        public Builder(Access access) {
            this.access = access;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Dashboard build() {
            Preconditions.checkNotNull(access);
            Preconditions.checkNotNull(referenceId);
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(token);
            try (DashboardSession session = openSession()) {
                return session.dashboardInsertOrUpdate(this);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Access getAccess() {
            return access;
        }

        public Long getId() {
            return id;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public String getName() {
            return name;
        }

        public String getToken() {
            return token;
        }

    }

}
