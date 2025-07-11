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
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.jackson.LocalDateTimeSerializer;
import page.foliage.common.util.DateTimes;
import page.foliage.ldap.Domain;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.io.Resources;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FlowDeployment {

    // ------------------------------------------------------------------------

    private final Deployment delegate;

    // ------------------------------------------------------------------------

    public FlowDeployment(Deployment delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    // ------------------------------------------------------------------------

    public static Builder builder(Domain domain) {
        return singleton().deploymentBuilder(domain);
    }

    // ------------------------------------------------------------------------

    public static PaginList<FlowDeployment> list(QueryParams params, Domain domain) {
        return singleton().deploymentsQueryByParamsAndDomain(params, domain);
    }

    // ------------------------------------------------------------------------

    public static FlowDeployment get(String id) {
        return singleton().deploymentQueryById(id);
    }

    // ------------------------------------------------------------------------

    public void remove() {
        singleton().deploymentDeleteById(getId());
    }

    public PaginList<FlowDefinition> definitions(QueryParams params) {
        return singleton().definitionsQueryByParamsAndDeploymentId(params, getId());
    }

    public List<FlowResource> resources() {
        return singleton().resourcesQueryByDeploymentId(getId());
    }

    public FlowResource resource() {
        return resource(FlowResource.NAME_DEFAULT);
    }

    public FlowResource resource(String name) {
        return resources().stream().filter(i -> StringUtils.equalsIgnoreCase(i.getName(), name)).findFirst().get();
    }

    public Domain domain() {
        return Domain.get(getTenantId());
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        FlowDeployment rhs = (FlowDeployment) obj;
        return new EqualsBuilder().append(delegate, rhs.delegate).isEquals();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    // ------------------------------------------------------------------------

    public String getId() {
        return delegate.getId();
    }

    public String getTenantId() {
        return delegate.getTenantId();
    }

    public String getKey() {
        return delegate.getKey();
    }

    public String getName() {
        return delegate.getName();
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getDeploymentTime() {
        return DateTimes.toLocalDateTime(delegate.getDeploymentTime());
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private final DeploymentBuilder builder;

        Builder(DeploymentBuilder builder) {
            this.builder = builder;
        }

        public Builder key(String key) {
            builder.key(key);
            return this;
        }

        public Builder name(String name) {
            builder.name(name);
            return this;
        }

        public Builder addResource(FlowResource.Builder resource) {
            try {
                builder.addBytes(resource.name, resource.source.read());
                return this;
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Builder addResource(String classpath) {
            try {
                URL url = Resources.getResource(classpath);
                builder.addBytes(FlowResource.NAME_DEFAULT, Resources.toByteArray(url));
                return this;
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public FlowDeployment deploy() {
            return new FlowDeployment(builder.deploy());
        }

    }

}
