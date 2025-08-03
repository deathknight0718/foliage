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

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskCompletionBuilder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.jackson.LocalDateTimeSerializer;
import page.foliage.common.util.CodecUtils;
import page.foliage.common.util.DateTimes;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.ldap.Access;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FlowTask {

    // ------------------------------------------------------------------------

    private final Task delegate;

    // ------------------------------------------------------------------------

    public FlowTask(Task delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    // ------------------------------------------------------------------------

    public static PaginList<FlowTask> list(Access access, QueryParams params) {
        return getInstance(FederatedEngine.class).taskQueryList(access, params);
    }

    public static FlowTask get(Access access, String id) {
        return getInstance(FederatedEngine.class).taskQueryById(access, id);
    }

    // ------------------------------------------------------------------------

    public Submitter submitter(Access access) {
        return getInstance(FederatedEngine.class).taskCompleting(access, this);
    }

    public FlowDefinition definition(Access access) {
        return FlowDefinition.get(access, getProcessDefinitionId());
    }

    // ------------------------------------------------------------------------

    public FlowVariables variables() {
        return getInstance(FederatedEngine.class).variablesQuery(this);
    }

    // ------------------------------------------------------------------------

    public void remove() {
        getInstance(FederatedEngine.class).taskDelete(this);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        FlowTask rhs = (FlowTask) obj;
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

    public String getName() {
        return delegate.getName();
    }

    public String getProcessId() {
        return delegate.getProcessInstanceId();
    }

    public String getProcessDefinitionId() {
        return delegate.getProcessDefinitionId();
    }

    public String getAssignee() {
        return delegate.getAssignee();
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getCreateTime() {
        return DateTimes.toLocalDateTime(delegate.getCreateTime());
    }

    public DelegationState getDelegationState() {
        return delegate.getDelegationState();
    }

    public String getTaskDefinitionKey() {
        return delegate.getTaskDefinitionKey();
    }

    public Boolean getSuspended() {
        return delegate.isSuspended();
    }

    // ------------------------------------------------------------------------

    public static class Submitter {

        private final TaskCompletionBuilder delegate;

        public Submitter(TaskCompletionBuilder delegate) {
            this.delegate = delegate;
        }

        public Submitter accessId(Long accessId) {
            variable(FlowVariables.KEY_ACCESS_ID, CodecUtils.encodeHex36(accessId));
            return this;
        }

        public Submitter assigneeId(Long assigneeId) {
            variable(FlowVariables.KEY_ASSIGNEE_ID, CodecUtils.encodeHex36(assigneeId));
            return this;
        }

        public Submitter referenceId(Long referenceId) {
            variable(FlowVariables.KEY_REFERENCE_ID, CodecUtils.encodeHex36(referenceId));
            return this;
        }

        public Submitter referenceType(String referenceType) {
            variable(FlowVariables.KEY_REFERENCE_TYPE, referenceType);
            return this;
        }

        public Submitter result(String result) {
            variable(FlowVariables.KEY_RESULT, result);
            return this;
        }

        public Submitter variable(String key, Object value) {
            delegate.variable(key, value);
            return this;
        }

        public Submitter variables(FlowVariables variables) {
            delegate.variables(variables);
            return this;
        }

        public void submit() {
            delegate.complete();
        }

    }

}
