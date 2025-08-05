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
import static page.foliage.common.util.CodecUtils.decodeHex36;
import static page.foliage.common.util.CodecUtils.encodeHex36;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.jackson.LocalDateTimeSerializer;
import page.foliage.common.util.DateTimes;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.ldap.Access;

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

    public static Starter builder(FlowDefinition definition) {
        return getInstance(FederatedEngine.class).processStarting(definition);
    }

    // ------------------------------------------------------------------------

    public static PaginList<FlowProcess> list(Access access, QueryParams params) {
        return getInstance(FederatedEngine.class).processQueryList(access, params);
    }

    public static FlowProcess get(Access access, String id) {
        return getInstance(FederatedEngine.class).processQuery(access, id);
    }

    // ------------------------------------------------------------------------

    public FlowExecution execution(Access access, String activityKey) {
        return getInstance(FederatedEngine.class).executionQuery(access, this, activityKey);
    }

    public PaginList<FlowTask> tasks(Access access, QueryParams params) {
        return getInstance(FederatedEngine.class).taskQueryList(access, params, this);
    }

    public FlowTask task(Access access, String key) {
        return getInstance(FederatedEngine.class).taskQueryByKey(access, this, key);
    }

    public PaginList<FlowHistoricActivity> historicActivities(Access access, QueryParams params) {
        return getInstance(FederatedEngine.class).historicActivitieQueryList(access, params, this);
    }

    public List<String> waitingIds() {
        return getInstance(FederatedEngine.class).waitingIdQueryList(this);
    }

    public Map<String, Object> variables() {
        return delegate.getProcessVariables();
    }

    public boolean isEnded() {
        return delegate.isEnded();
    }

    public void terminate(String reason) {
        Preconditions.checkState(!isEnded(), "Process has already ended.");
        getInstance(FederatedEngine.class).processDelete(this, reason);
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

    public Long getDomainId() {
        return decodeHex36(delegate.getTenantId());
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

    public String getBusinessKey() {
        return delegate.getBusinessKey();
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

    public static class Starter {

        @SuppressWarnings("unused")
        private final FlowDefinition definition;

        private final ProcessInstanceBuilder delegate;

        Starter(FlowDefinition definition, RuntimeService service) {
            this.definition = definition;
            this.delegate = service.createProcessInstanceBuilder();
            this.delegate.tenantId(definition.getTenantId());
            this.delegate.processDefinitionId(definition.getId());
        }

        public Starter assigneeId(Long assigneeId) {
            delegate.assignee(encodeHex36(assigneeId));
            delegate.variable(FlowKeys.KEY_ASSIGNEE_ID, encodeHex36(assigneeId));
            return this;
        }

        public Starter referenceId(Long referenceId) {
            delegate.referenceId(encodeHex36(referenceId));
            delegate.variable(FlowKeys.KEY_REFERENCE_ID, encodeHex36(referenceId));
            return this;
        }

        public Starter referenceType(String referenceType) {
            delegate.referenceType(referenceType);
            delegate.variable(FlowKeys.KEY_REFERENCE_TYPE, referenceType);
            return this;
        }

        public Starter result(String result) {
            delegate.variable(FlowKeys.KEY_RESULT, result);
            return this;
        }

        public Starter name(String name) {
            delegate.name(name);
            return this;
        }

        public Starter variable(String key, Object value) {
            delegate.variable(key, value);
            return this;
        }

        public Starter variables(Map<String, Object> variables) {
            delegate.variables(variables);
            return this;
        }

        public FlowProcess start() {
            return new FlowProcess(delegate.start());
        }

    }

}
