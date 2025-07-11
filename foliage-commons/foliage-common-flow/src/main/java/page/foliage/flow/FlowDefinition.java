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

import java.io.InputStream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.flowable.engine.repository.ProcessDefinition;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.ldap.Domain;
import page.foliage.ldap.User;
import page.foliage.guava.common.base.Preconditions;

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

    public static PaginList<FlowDefinition> list(QueryParams params, Domain domain) {
        return singleton().definitionsQueryByParamsAndDomain(params, domain);
    }

    public static FlowDefinition get(String id) {
        return singleton().definitionQueryById(id);
    }

    public static FlowDefinition latest(String key, Domain domain) {
        return singleton().definitionQueryByKeyAndDomain(key, domain);
    }

    // ------------------------------------------------------------------------

    public FlowProcess.Builder starter() {
        Preconditions.checkArgument(!delegate.hasStartFormKey());
        return FlowProcess.builder(Domain.get(getTenantId())).definitionId(getId());
    }

    public FlowProcess.Builder starter(User user) {
        FlowProcess.Builder builder = FlowProcess.builder(user).definitionId(getId());
        if (delegate.hasStartFormKey()) {
            String key = singleton().formKeyQueryByDefinitionId(getId());
            builder.formKey(key);
        }
        return builder;
    }

    public PaginList<FlowProcess> processes(QueryParams params) {
        return singleton().processesQueryByParamsAndDefinitionId(params, getId());
    }

    // ------------------------------------------------------------------------

    public FormResource formOfStart() {
        Preconditions.checkArgument(delegate.hasStartFormKey());
        String key = singleton().formKeyQueryByDefinitionId(getId());
        return FormResource.get(key, Domain.get(getTenantId()));
    }

    public FormResource formOfTask(String taskKey) {
        String key = singleton().formKeyQueryByDefinitionIdAndTaskKey(getId(), taskKey);
        return FormResource.get(key, Domain.get(getTenantId()));
    }

    public Domain domain() {
        return Domain.get(getTenantId());
    }

    // ------------------------------------------------------------------------

    public InputStream streamOfImage() {
        return singleton().streamForDiagramByDefinitionId(getId());
    }

    public InputStream streamOfModel() {
        return singleton().streamForModelByDefinitionId(getId());
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
