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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskCompletionBuilder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.jackson.LocalDateTimeSerializer;
import page.foliage.common.util.DateTimes;
import page.foliage.ldap.Domain;
import page.foliage.ldap.User;
import page.foliage.guava.common.base.Preconditions;

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

    public static PaginList<FlowTask> list(QueryParams params, Domain domain) {
        return singleton().tasksQueryByParamsAndDomain(params, domain);
    }

    public static PaginList<FlowTask> list(QueryParams params, User user) {
        return singleton().tasksQueryByParamsAndUser(params, user);
    }

    public static FlowTask get(String id) {
        return singleton().taskQueryById(id);
    }

    // ------------------------------------------------------------------------

    public SubmitionBuilder submitter(User user) {
        return singleton().taskCompletionBuild(user, getProcessId(), getId(), getFormKey());
    }

    public FormResource resource() {
        return FormResource.get(getFormKey(), Domain.get(getTenantId()));
    }

    public List<FormPayloadReference> references() {
        try (FederatedSession session = singleton().openSession()) {
            return session.referencesSelectByProcessId(getProcessId());
        } catch (SQLException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public FlowDefinition definition() {
        return FlowDefinition.get(getProcessDefinitionId());
    }

    // ------------------------------------------------------------------------

    public List<FormPayloadReference> payloads() {
        try (FederatedSession session = singleton().openSession()) {
            return session.referencesSelectByProcessId(getProcessId());
        } catch (SQLException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public Domain domain() {
        return Domain.get(getTenantId());
    }

    public FlowVariables variables() {
        return singleton().variablesQueryByTaskId(getId());
    }

    // ------------------------------------------------------------------------

    public void remove() {
        singleton().taskDeleteById(getId());
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

    public String getFormKey() {
        String formKey = delegate.getFormKey();
        return formKey != null ? formKey : FormResource.KEY_DEFAULT;
    }

    public String getTaskDefinitionKey() {
        return delegate.getTaskDefinitionKey();
    }

    // ------------------------------------------------------------------------

    public static class SubmitionBuilder {

        private final TaskCompletionBuilder delegate;

        private String processId, taskId, formKey;

        private FormPayload payload;

        private FlowVariables variables = new FlowVariables();

        public SubmitionBuilder(TaskCompletionBuilder delegate) {
            this.delegate = delegate;
        }

        public SubmitionBuilder formKey(String formKey) {
            this.formKey = formKey;
            return this;
        }

        public SubmitionBuilder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public SubmitionBuilder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public SubmitionBuilder payload(FormPayload payload) {
            this.payload = payload;
            return this;
        }

        public SubmitionBuilder variable(String key, Object value) {
            variables.put(key, value);
            return this;
        }

        public SubmitionBuilder variables(FlowVariables variables) {
            variables.putAll(variables);
            return this;
        }

        public void complete() {
            Preconditions.checkNotNull(taskId);
            if (payload != null) {
                FormPayloadReference.Builder builder = FormPayloadReference.builder();
                builder.source(payload);
                variables.payload(builder.source);
                builder.key(formKey).executionId(taskId).build(processId);
            }
            variables.put(FlowStatus.VARIABLE_STATUS, FlowStatus.PASSED.name());
            delegate.variables(variables);
            delegate.complete();
        }
        
        public void reject() {
            Preconditions.checkNotNull(taskId);
            variables.put(FlowStatus.VARIABLE_STATUS, FlowStatus.REJECTED.name());
            delegate.variables(variables);
            delegate.complete();
        }

    }

}
