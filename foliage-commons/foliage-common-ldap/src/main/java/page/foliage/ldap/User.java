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

import static page.foliage.common.ioc.InstanceFactory.getInstance;

import java.io.Serializable;
import java.util.Collections;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.jackson.Hex36Serializer;
import page.foliage.common.util.CodecUtils;
import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.ImmutableSet;
import page.foliage.ldap.session.IdentitySession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class User implements Serializable {

    // ------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------------

    private Long id;

    private String name, displayName;

    private String email;

    private Long domainId;

    // ------------------------------------------------------------------------

    private User() {}

    // ------------------------------------------------------------------------

    public static User get(Long id) {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.userSelectById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static User fromEmail(String email) {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.userSelectByEmail(email);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static User fromName(String name) {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.userSelectByName(name);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    public Domain domain() {
        return Domain.get(domainId);
    }

    // ------------------------------------------------------------------------

    public boolean isMemberOf(Role... roles) {
        return !Collections.disjoint(ImmutableSet.copyOf(roles), roles(QueryParams.ALL));
    }

    public PaginList<Role> roles(QueryParams params) {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.rolesSelectByParamsAndUserId(params, id);
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
        User rhs = (User) obj;
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

    public String getHexId() {
        return CodecUtils.encodeHex36(id);
    }

    public String getTenantId() {
        return CodecUtils.encodeHex36(domainId);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    @JsonSerialize(using = Hex36Serializer.class)
    public Long getDomainId() {
        return domainId;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private final User bean = new User();

        public Builder(Long id) {
            this.bean.id = id;
        }

        public Builder name(String name) {
            bean.name = name;
            return this;
        }

        public Builder displayName(String displayName) {
            bean.displayName = displayName;
            return this;
        }

        public Builder email(String email) {
            bean.email = email;
            return this;
        }

        public Builder domainId(Long domainId) {
            bean.domainId = domainId;
            return this;
        }

        public User build() {
            Preconditions.checkNotNull(bean.id, "User ID cannot be null");
            Preconditions.checkNotNull(bean.name, "User name cannot be null");
            Preconditions.checkNotNull(bean.domainId, "User domain ID cannot be null");
            Preconditions.checkNotNull(bean.displayName, "User display name cannot be null");
            Preconditions.checkNotNull(bean.email, "User email cannot be null");
            return bean;
        }

    }

}
