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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.jackson.Hex36Serializer;
import page.foliage.common.util.CodecUtils;
import page.foliage.guava.common.base.Objects;
import page.foliage.ldap.session.IdentitySession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Domain implements Serializable {

    // ------------------------------------------------------------------------

    public static final Domain SYSTEM = new Domain(0L, "system", "平台管理中心");

    // ------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------------

    private final Long id;

    private final String identifier;

    private final String displayName;

    // ------------------------------------------------------------------------

    public Domain(Long id, String identifier, String displayName) {
        this.id = id;
        this.identifier = identifier;
        this.displayName = displayName;
    }

    // ------------------------------------------------------------------------

    public static PaginList<Domain> list(QueryParams params) {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.domainsSelectByParams(params);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Domain get(Long id) {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.domainSelectById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Domain get(String identifier) {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.domainSelectByIdentifier(identifier);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    public PaginList<Domain> members(QueryParams params) {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.domainsRecursionByParamsAndRoot(params, this);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public PaginList<User> users(QueryParams params) {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.usersSelectByParamsAndDomainId(params, id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Domain parent() {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.domainParentById(id);
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

    @JsonSerialize(using = Hex36Serializer.class)
    public Long getId() {
        return id;
    }

    public String getHexId() {
        return CodecUtils.encodeHex36(id);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

}
