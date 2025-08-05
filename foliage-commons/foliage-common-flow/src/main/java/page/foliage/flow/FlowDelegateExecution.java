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

import static page.foliage.common.util.CodecUtils.decodeHex36;

import java.util.Map;

import org.flowable.engine.delegate.DelegateExecution;

import page.foliage.common.util.CodecUtils;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FlowDelegateExecution {

    // ------------------------------------------------------------------------

    private final DelegateExecution delegate;

    // ------------------------------------------------------------------------

    public FlowDelegateExecution(DelegateExecution delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------

    public Map<String, Object> map() {
        return delegate.getVariables();
    }

    // ------------------------------------------------------------------------

    public String get(String k) {
        return delegate.getVariable(k, String.class);
    }

    public Long getHex36(String k) {
        return CodecUtils.decodeHex36(delegate.getVariable(k, String.class));
    }

    // ------------------------------------------------------------------------

    public void set(String k, String v) {
        delegate.setVariable(k, v);
    }

    public void setHex36(String k, Long v) {
        delegate.setVariable(k, CodecUtils.encodeHex36(v));
    }

    // ------------------------------------------------------------------------

    public Long getReferenceId() {
        return getHex36(FlowKeys.KEY_REFERENCE_ID);
    }

    public Long getReferenceId(String definitionKey) {
        return getHex36(FlowKeys.prefix(definitionKey, FlowKeys.KEY_REFERENCE_ID));
    }

    public String getReferenceType() {
        return get(FlowKeys.KEY_REFERENCE_TYPE);
    }

    public String getReferenceType(String definitionKey) {
        return get(FlowKeys.prefix(definitionKey, FlowKeys.KEY_REFERENCE_TYPE));
    }

    public Long getAssigneeId() {
        return getHex36(FlowKeys.KEY_ASSIGNEE_ID);
    }

    public Long getAssigneeId(String definitionKey) {
        return getHex36(FlowKeys.prefix(definitionKey, FlowKeys.KEY_ASSIGNEE_ID));
    }

    public String getResult() {
        return get(FlowKeys.KEY_RESULT);
    }

    public String getResult(String definitionKey) {
        return get(FlowKeys.prefix(definitionKey, FlowKeys.KEY_RESULT));
    }

    public String getResultReason() {
        return get(FlowKeys.KEY_RESULT_REASON);
    }

    public String getResultReason(String definitionKey) {
        return get(FlowKeys.prefix(definitionKey, FlowKeys.KEY_RESULT_REASON));
    }

    public String getResultReferenceId() {
        return get(FlowKeys.KEY_RESULT_REFERENCE_ID);
    }

    public String getResultReferenceId(String definitionKey) {
        return get(FlowKeys.prefix(definitionKey, FlowKeys.KEY_RESULT_REFERENCE_ID));
    }

    // ------------------------------------------------------------------------

    public void setReferenceId(String definitionKey, Long referenceId) {
        setHex36(FlowKeys.prefix(definitionKey, FlowKeys.KEY_REFERENCE_ID), referenceId);
    }

    public void setReferenceType(String definitionKey, String referenceType) {
        set(FlowKeys.prefix(definitionKey, FlowKeys.KEY_REFERENCE_TYPE), referenceType);
    }

    public void setAssigneeId(String definitionKey, Long assigneeId) {
        setHex36(FlowKeys.prefix(definitionKey, FlowKeys.KEY_ASSIGNEE_ID), assigneeId);
    }

    public void setResult(String definitionKey, String result) {
        set(FlowKeys.prefix(definitionKey, FlowKeys.KEY_RESULT), result);
    }

    public void setResultReason(String definitionKey, String resultReason) {
        set(FlowKeys.prefix(definitionKey, FlowKeys.KEY_RESULT_REASON), resultReason);
    }

    public void setResultReferenceId(String definitionKey, String resultReferenceId) {
        set(FlowKeys.prefix(definitionKey, FlowKeys.KEY_RESULT_REFERENCE_ID), resultReferenceId);
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

    public String getCurrentActivityId() {
        return delegate.getCurrentActivityId();
    }

    public String getProcessInstanceId() {
        return delegate.getProcessInstanceId();
    }

    public String getProcessDefinitionId() {
        return delegate.getProcessDefinitionId();
    }

}
