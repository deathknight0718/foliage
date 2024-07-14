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

import static page.foliage.ldap.session.IdentitySessionFactory.openSession;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import page.foliage.common.collect.QueryParams;
import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.net.HostAndPort;
import page.foliage.ldap.session.IdentitySession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Repository {

    // ----------------------------------------------------------------------------

    private final Long id;

    private String name, displayName;

    private Long domainId;

    private HostAndPort hostAndPort;

    // ----------------------------------------------------------------------------

    private Repository(Long id) {
        this.id = id;
    }

    // ------------------------------------------------------------------------

    public static Repository create(Long id) {
        return new Repository(id);
    }

    public static Repository get(Long id) {
        try (IdentitySession session = openSession()) {
            return session.repositorySelectById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ----------------------------------------------------------------------------

    public Boolean isActive() {
        return true;
    }

    public List<Contract> contracts(QueryParams params) {
        try (IdentitySession session = openSession()) {
            return session.contractsSelectByParamsAndRepositoryId(params, id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ----------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        Repository rhs = (Repository) obj;
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

    // ----------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }

    public void setHostAndPort(HostAndPort hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

}
