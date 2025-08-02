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

    public FlowVariables getVariables() {
        return new FlowVariables(delegate.getVariables());
    }

    public FlowVariables.OptionalValue getVariable(String name) {
        return getVariables().val(name);
    }

    public FlowVariables getLocalVariables() {
        return new FlowVariables(delegate.getVariablesLocal());
    }

    public FlowVariables.OptionalValue getLocalVariable(String name) {
        return getLocalVariables().val(name);
    }

    // ------------------------------------------------------------------------

    public Long getReferenceId() {
        return CodecUtils.decodeHex36(delegate.getVariable(FlowVariables.KEY_REFERENCE_ID, String.class));
    }

    public String getReferenceType() {
        return delegate.getVariable(FlowVariables.KEY_REFERENCE_TYPE, String.class);
    }

    public Long getAccessId() {
        return CodecUtils.decodeHex36(delegate.getVariable(FlowVariables.KEY_ACCESS_ID, String.class));
    }

    public Long getAssigneeId() {
        return CodecUtils.decodeHex36(delegate.getVariable(FlowVariables.KEY_ASSIGNEE_ID, String.class));
    }

    public String getResult() {
        return delegate.getVariable(FlowVariables.KEY_RESULT, String.class);
    }

    public String getResultReason() {
        return delegate.getVariable(FlowVariables.KEY_RESULT_REASON, String.class);
    }

    public String getResultReferenceId() {
        return delegate.getVariable(FlowVariables.KEY_RESULT_REFERENCE_ID, String.class);
    }

    // ------------------------------------------------------------------------

    public void setVariables(FlowVariables variables) {
        delegate.setVariables(variables);
    }

    public void setVariable(String key, Object value) {
        delegate.setVariable(key, value);
    }

    public void setLocalVariables(FlowVariables variables) {
        delegate.setVariablesLocal(variables);
    }

    public void setLocalVariable(String key, Object value) {
        delegate.setVariableLocal(key, value);
    }

    // ------------------------------------------------------------------------

    public void setReferenceId(Long referenceId) {
        delegate.setVariable(FlowVariables.KEY_REFERENCE_ID, CodecUtils.encodeHex36(referenceId));
    }

    public void setReferenceType(String referenceType) {
        delegate.setVariable(FlowVariables.KEY_REFERENCE_TYPE, referenceType);
    }

    public void setAccessId(Long accessId) {
        delegate.setVariable(FlowVariables.KEY_ACCESS_ID, CodecUtils.encodeHex36(accessId));
    }

    public void setAssigneeId(Long assigneeId) {
        delegate.setVariable(FlowVariables.KEY_ASSIGNEE_ID, CodecUtils.encodeHex36(assigneeId));
    }

    public void setResult(String result) {
        delegate.setVariable(FlowVariables.KEY_RESULT, result);
    }

    public void setResultReason(String resultReason) {
        delegate.setVariable(FlowVariables.KEY_RESULT_REASON, resultReason);
    }

    public void setResultReferenceId(String resultReferenceId) {
        delegate.setVariable(FlowVariables.KEY_RESULT_REFERENCE_ID, resultReferenceId);
    }

    // ------------------------------------------------------------------------

    public String getId() {
        return delegate.getId();
    }

    public String getCurrentActivityId() {
        return delegate.getCurrentActivityId();
    }

}
