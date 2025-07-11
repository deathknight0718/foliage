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
package page.foliage.flow;

import static page.foliage.flow.FederatedEngine.singleton;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.common.jackson.LocalDateTimeSerializer;
import page.foliage.common.util.JsonNodes;
import page.foliage.ldap.Access;
import page.foliage.ldap.Domain;
import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.guava.common.io.ByteSource;
import page.foliage.guava.common.io.Resources;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FormResource {

    // ------------------------------------------------------------------------

    public final static String KEY_DEFAULT = "default";

    // ------------------------------------------------------------------------

    private final Long id;

    private String tenantId, key, name, description;

    private LocalDateTime updateTime;

    // ------------------------------------------------------------------------

    FormResource(Long id) {
        this.id = id;
    }

    // ------------------------------------------------------------------------

    private static JuelExpressionFactory juel() {
        return InstanceFactory.getInstance(JuelExpressionFactory.class);
    }

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    public static PaginList<FormResource> list(QueryParams params, Domain domain) {
        try (FederatedSession session = singleton().openSession()) {
            return session.resourcesSelectByParamsAndDomain(params, domain);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static FormResource defaultIfAbset(Domain domain) {
        try (FederatedSession session = singleton().openSession()) {
            return session.resourceDefaultByDomain(domain);
        } catch (SQLException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static FormResource get(String key, Domain domain) {
        try (FederatedSession session = singleton().openSession()) {
            return session.resourceSelectByKeyAndDomain(key, domain);
        } catch (SQLException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static FormResource get(Long id) {
        try (FederatedSession session = singleton().openSession()) {
            return session.resourceSelectById(id);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    public FormPayload express(Access access) {
        Preconditions.checkArgument(Objects.equal(access.getDomain(), Domain.get(getTenantId())));
        return doExpress(stream(), ImmutableMap.of(FormPayload.KEYWORD_ACCESS, access));
    }
    
    public FormPayload express(Access access, FlowVariables variables) {
        Preconditions.checkArgument(Objects.equal(access.getDomain(), Domain.get(getTenantId())));
        variables.put(FormPayload.KEYWORD_ACCESS, access);
        return doExpress(stream(), variables);
    }

    public InputStream stream() {
        try (FederatedSession session = singleton().openSession()) {
            return session.resourcePayloadAsStreamById(id);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Domain domain() {
        return Domain.get(tenantId);
    }

    public Long remove() {
        try (FederatedSession session = singleton().openSession()) {
            return session.resourceDeleteById(id);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    private static FormPayload doExpress(InputStream input, Map<String, Object> variables) {
        try {
            String jsonText = juel().express(variables, input).invoke();
            return new FormPayload(JsonNodes.asNode(jsonText));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        FormResource rhs = (FormResource) obj;
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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        Long id;

        String key, name, tenantId, description;

        ByteSource resource;

        Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder defaultKey() {
            this.key = KEY_DEFAULT;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder access(Access access) {
            this.tenantId = access.getDomain().getIdentifier();
            return this;
        }

        public Builder domain(Domain domain) {
            this.tenantId = domain.getIdentifier();
            return this;
        }

        public Builder resource(String classpath) {
            return resource(Resources.asByteSource(Resources.getResource(classpath)));
        }

        public Builder resource(InputStream input) {
            try (input) {
                return resource(ByteSource.wrap(IOUtils.toByteArray(input)));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Builder resource(ByteSource resource) {
            this.resource = resource;
            return this;
        }

        public FormResource build() {
            try (FederatedSession session = singleton().openSession()) {
                return build(session);
            } catch (SQLException | IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        FormResource build(FederatedSession session) throws SQLException, IOException {
            Preconditions.checkNotNull(resource);
            Preconditions.checkNotNull(key);
            Preconditions.checkNotNull(name);
            return session.resourceInsertOrUpdateByBuilder(this);
        }

    }

}
