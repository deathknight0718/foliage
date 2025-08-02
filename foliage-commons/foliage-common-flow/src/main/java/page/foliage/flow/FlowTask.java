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

    public static PaginList<FlowTask> list(QueryParams params) {
        return getInstance(FederatedEngine.class).taskQueryList(Access.current(), params);
    }

    public static FlowTask get(String id) {
        return getInstance(FederatedEngine.class).taskQueryById(Access.current(), id);
    }

    // ------------------------------------------------------------------------

    public Completer submitter() {
        return getInstance(FederatedEngine.class).taskCompleting(Access.current(), this);
    }

    public FlowDefinition definition() {
        return FlowDefinition.get(getProcessDefinitionId());
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

    // ------------------------------------------------------------------------

    public static class Completer {

        private final TaskCompletionBuilder delegate;

        private FlowVariables variables = new FlowVariables();

        public Completer(TaskCompletionBuilder delegate) {
            this.delegate = delegate;
        }

        public Completer accessId(Long accessId) {
            variables.put(FlowVariables.KEY_ACCESS_ID, CodecUtils.encodeHex36(accessId));
            return this;
        }

        public Completer variable(String key, Object value) {
            variables.put(key, value);
            return this;
        }

        public Completer variables(FlowVariables variables) {
            variables.putAll(variables);
            return this;
        }

        public void complete() {
            variables.put(FlowStatus.VARIABLE_STATUS, FlowStatus.PASSED.name());
            delegate.variables(variables);
            delegate.complete();
        }

        public void reject() {
            variables.put(FlowStatus.VARIABLE_STATUS, FlowStatus.REJECTED.name());
            delegate.variables(variables);
            delegate.complete();
        }

    }

}
