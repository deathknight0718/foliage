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

import static page.foliage.common.ioc.InstanceFactory.getInstance;

import java.io.InputStream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.io.ByteSource;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FlowResource {

    // ------------------------------------------------------------------------
    
    public final static String NAME_DEFAULT = "default.bpmn";

    // ------------------------------------------------------------------------

    private final String name, deploymentId;

    // ------------------------------------------------------------------------

    public FlowResource(String name, String deploymentId) {
        this.name = name;
        this.deploymentId = deploymentId;
    }

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    public InputStream stream() {
        return getInstance(FederatedEngine.class).streamForResource(deploymentId, name);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        FlowResource rhs = (FlowResource) obj;
        return new EqualsBuilder().append(name, rhs.name).append(deploymentId, rhs.deploymentId).isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, deploymentId);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    // ------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        String name = NAME_DEFAULT;

        ByteSource source;

        Builder() {}

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder source(ByteSource source) {
            this.source = source;
            return this;
        }

    }

}
