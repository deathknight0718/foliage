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

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.collect.Identities;
import page.foliage.common.jackson.LocalDateTimeSerializer;
import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.ldap.session.IdentitySession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Contract {

    // ------------------------------------------------------------------------

    private final Long id;

    private String name;

    private Long domainId;

    private Long repositoryId;

    private LocalDateTime expiration;

    // ------------------------------------------------------------------------

    private Contract(Long id) {
        this.id = id;
    }

    // ------------------------------------------------------------------------

    public static Builder builder(Access access) {
        return new Contract.Builder(access);
    }

    // ------------------------------------------------------------------------

    public static Contract create(Long id) {
        return new Contract(id);
    }

    public static Contract get(Long id) {
        try (IdentitySession session = openSession()) {
            return session.contractSelectById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    public Repository repository() {
        return Repository.get(repositoryId);
    }

    public Contract domain() {
        return Contract.get(domainId);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration);
    }

    public void remove() {
        try (IdentitySession session = openSession()) {
            session.contractDeleteById(id);
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
        Contract rhs = (Contract) obj;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private final Long id;

        private final Access access;

        private String name;

        private Domain domain;

        private Repository repository;

        private LocalDateTime expiration;

        public Builder(Access access) {
            this.id = Identities.jsonflake();
            this.access = access;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder domainId(Long domainId) {
            this.domain = Preconditions.checkNotNull(Domain.get(domainId));
            return this;
        }

        public Builder repositoryId(Long repositoryId) {
            this.repository = Preconditions.checkNotNull(Repository.get(repositoryId));;
            return this;
        }

        public Builder expiration(LocalDateTime expiration) {
            this.expiration = expiration;
            return this;
        }

        public Contract build() {
            Preconditions.checkNotNull(name);
            Preconditions.checkArgument(expiration.isAfter(LocalDateTime.now()));
            try (IdentitySession session = openSession()) {
                return session.contractInsert(this);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Access getAccess() {
            return access;
        }

        public Domain getDomain() {
            return domain;
        }

        public Repository getRepository() {
            return repository;
        }

        public LocalDateTime getExpiration() {
            return expiration;
        }

    }

}
