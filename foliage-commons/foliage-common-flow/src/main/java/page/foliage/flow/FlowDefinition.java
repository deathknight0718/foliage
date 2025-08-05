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
import org.flowable.engine.repository.ProcessDefinition;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.ldap.Access;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FlowDefinition {

    // ------------------------------------------------------------------------

    private final ProcessDefinition delegate;

    // ------------------------------------------------------------------------

    public FlowDefinition(ProcessDefinition delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    // ------------------------------------------------------------------------

    public static PaginList<FlowDefinition> list(Access access, QueryParams params) {
        return getInstance(FederatedEngine.class).definitionQueryList(access, params);
    }

    public static FlowDefinition get(Access access, String id) {
        return getInstance(FederatedEngine.class).definitionQueryById(access, id);
    }

    public static FlowDefinition latest(Access access, String key) {
        return getInstance(FederatedEngine.class).definitionQueryByKey(access, key);
    }

    // ------------------------------------------------------------------------

    public FlowProcess.Starter starter(Access access) {
        return FlowProcess.builder(access, this);
    }

    public PaginList<FlowProcess> processes() {
        return getInstance(FederatedEngine.class).processQueryList(this);
    }

    // ------------------------------------------------------------------------

    public InputStream streamOfImage() {
        return getInstance(FederatedEngine.class).streamForDiagram(this);
    }

    public InputStream streamOfModel() {
        return getInstance(FederatedEngine.class).streamForModel(this);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        FlowDefinition rhs = (FlowDefinition) obj;
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

    public String getDeploymentId() {
        return delegate.getDeploymentId();
    }

    public String getCategory() {
        return delegate.getCategory();
    }

    public String getName() {
        return delegate.getName();
    }

    public String getKey() {
        return delegate.getKey();
    }

    public String getDescription() {
        return delegate.getDescription();
    }

    public InitiationType getInitiationType() {
        return delegate.hasStartFormKey() ? InitiationType.FORM_DRIVEN : InitiationType.AUTOMATIC;
    }

    // ------------------------------------------------------------------------

    public static enum InitiationType {
        AUTOMATIC, FORM_DRIVEN
    }

}
