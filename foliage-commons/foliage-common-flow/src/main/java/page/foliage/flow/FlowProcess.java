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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;

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
public class FlowProcess {

    // ------------------------------------------------------------------------

    private final ProcessInstance delegate;

    // ------------------------------------------------------------------------

    public FlowProcess(ProcessInstance delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    // ------------------------------------------------------------------------

    public static Builder builder(Domain domain) {
        return singleton().processBuild(domain);
    }

    public static Builder builder(User user) {
        return singleton().processBuild(user);
    }

    // ------------------------------------------------------------------------

    public static PaginList<FlowProcess> list(QueryParams params, Domain domain) {
        return singleton().processesQueryByParamsAndDomain(params, domain);
    }

    public static PaginList<FlowProcess> list(QueryParams params, User user) {
        return singleton().processesQueryByParamsAndUser(params, user);
    }

    public static FlowProcess get(String processId) {
        return singleton().processQueryById(processId);
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowTask> tasks(QueryParams params) {
        return singleton().tasksQueryByParamsAndProcessId(params, getId());
    }

    public PaginList<FlowHistoricActivity> historicActivities(QueryParams params) {
        return singleton().historicActivitiesQueryByParamsAndProcessId(params, getId());
    }

    public FlowVariables variables() {
        return new FlowVariables(delegate.getProcessVariables());
    }

    public List<FormPayloadReference> references() {
        try (FederatedSession session = singleton().openSession()) {
            return session.referencesSelectByProcessId(getId());
        } catch (SQLException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public Domain domain() {
        return Domain.get(getTenantId());
    }

    public boolean isEnded() {
        return delegate.isEnded();
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        FlowProcess rhs = (FlowProcess) obj;
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

    public String getDefinitionId() {
        return delegate.getProcessDefinitionId();
    }

    public String getActivityId() {
        return delegate.getActivityId();
    }

    public String getCallbackId() {
        return delegate.getCallbackId();
    }

    public String getCallbackType() {
        return delegate.getCallbackType();
    }

    public String getReferenceId() {
        return delegate.getReferenceId();
    }

    public String getReferenceType() {
        return delegate.getReferenceType();
    }

    public String getStartUserId() {
        return delegate.getStartUserId();
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getStartTime() {
        Date time = delegate.getStartTime();
        return time == null ? null : DateTimes.toLocalDateTime(time);
    }

    public String getDescription() {
        return delegate.getDescription();
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private final ProcessInstanceBuilder delegate;

        private FormPayload payload;

        private String formKey;

        private FlowVariables variables = new FlowVariables();

        Builder(ProcessInstanceBuilder delegate) {
            this.delegate = delegate;
        }

        public Builder formKey(String formKey) {
            this.formKey = formKey;
            return this;
        }

        public Builder definitionId(String definitionId) {
            delegate.processDefinitionId(definitionId);
            return this;
        }

        public Builder name(String name) {
            delegate.name(name);
            return this;
        }

        public Builder payload(FormPayload payload) {
            this.payload = payload;
            return this;
        }

        public Builder variable(String key, Object value) {
            this.variables.put(key, value);
            return this;
        }

        public Builder variables(FlowVariables variables) {
            this.variables.putAll(variables);
            return this;
        }

        public FlowProcess start() {
            if (payload != null) {
                FormPayloadReference.Builder builder = FormPayloadReference.builder();
                builder.source(payload);
                delegate.variables(variables.payload(payload));
                ProcessInstance instance = delegate.start();
                builder.key(formKey).build(instance.getProcessInstanceId());
                return new FlowProcess(instance);
            }
            delegate.variables(variables);
            return new FlowProcess(delegate.start());
        }

    }

}
