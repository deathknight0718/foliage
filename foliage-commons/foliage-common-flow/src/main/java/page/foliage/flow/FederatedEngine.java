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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.Lists;
import page.foliage.ldap.Access;
import page.foliage.ldap.Domain;
import page.foliage.ldap.Role;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FederatedEngine {

    // ------------------------------------------------------------------------

    public final static String KEYWORD_KEY = "key";

    // ------------------------------------------------------------------------

    private final ProcessEngine processEngine;

    // ------------------------------------------------------------------------

    public FederatedEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowDeployment> deploymentQueryList(Access access, QueryParams params) {
        Preconditions.checkState(access.match(Role.SYSTEM_ADMIN, Role.DOMAIN_ADMIN), "Invalid access");
        DeploymentQuery query = processEngine.getRepositoryService().createDeploymentQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.deploymentTenantId(access.getDomainHexId());
        if (params.containsKey(KEYWORD_KEY)) query.deploymentKey(params.get(KEYWORD_KEY));
        List<Deployment> deployments = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(deployments, FlowDeployment::new), query.count());
    }

    public FlowDeployment deploymentQueryById(Access access, String id) {
        Preconditions.checkState(access.match(Role.SYSTEM_ADMIN, Role.DOMAIN_ADMIN), "Invalid access");
        DeploymentQuery query = processEngine.getRepositoryService().createDeploymentQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.deploymentTenantId(access.getDomainHexId());
        Deployment bean = query.deploymentId(id).singleResult();
        return Optional.ofNullable(bean).map(FlowDeployment::new).orElse(null);
    }

    public FlowDeployment deploymentQueryByKey(Access access, String key) {
        Preconditions.checkState(access.match(Role.SYSTEM_ADMIN, Role.DOMAIN_ADMIN), "Invalid access");
        DeploymentQuery query = processEngine.getRepositoryService().createDeploymentQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.deploymentTenantId(access.getDomainHexId());
        Deployment bean = query.deploymentKey(key).latest().singleResult();
        return Optional.ofNullable(bean).map(FlowDeployment::new).orElse(null);
    }

    public FlowDeployment.Builder deploymentBuilding(Access access, Domain domain) {
        Preconditions.checkState(access.match(Role.SYSTEM_ADMIN, Role.DOMAIN_ADMIN), "Invalid access");
        DeploymentBuilder builder = processEngine.getRepositoryService().createDeployment().tenantId(domain.getHexId());
        return new FlowDeployment.Builder(builder);
    }

    public void deploymentDelete(FlowDeployment deployment) {
        processEngine.getRepositoryService().deleteDeployment(deployment.getId(), true);
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowDefinition> definitionQueryList(Access access, QueryParams params) {
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.processDefinitionTenantId(access.getDomainHexId());
        if (!access.match(Role.DOMAIN_ADMIN)) query.startableByUser(access.getHexId());
        if (params.containsKey(KEYWORD_KEY)) query.processDefinitionKey(params.get(KEYWORD_KEY));
        List<ProcessDefinition> definitions = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(definitions, FlowDefinition::new), query.count());
    }

    public PaginList<FlowDefinition> definitionQueryList(FlowDeployment deployment) {
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        query.processDefinitionTenantId(deployment.getTenantId());
        List<ProcessDefinition> definitions = query.deploymentId(deployment.getId()).list();
        return PaginList.copyOf(Lists.transform(definitions, FlowDefinition::new), definitions.size());
    }

    public FlowDefinition definitionQueryById(Access access, String id) {
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.processDefinitionTenantId(access.getDomainHexId());
        if (!access.match(Role.DOMAIN_ADMIN)) query.startableByUser(access.getHexId());
        ProcessDefinition bean = query.processDefinitionId(id).singleResult();
        return Optional.ofNullable(bean).map(FlowDefinition::new).orElse(null);
    }

    public FlowDefinition definitionQueryByKey(Access access, String key) {
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.processDefinitionTenantId(access.getDomainHexId());
        if (!access.match(Role.DOMAIN_ADMIN)) query.startableByUser(access.getHexId());
        ProcessDefinition bean = query.processDefinitionKey(key).latestVersion().singleResult();
        return Optional.ofNullable(bean).map(FlowDefinition::new).orElse(null);
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowProcess> processQueryList(Access access, QueryParams params) {
        ProcessInstanceQuery query = processEngine.getRuntimeService().createProcessInstanceQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.processInstanceTenantId(access.getDomainHexId());
        if (!access.match(Role.DOMAIN_ADMIN)) query.involvedUser(access.getHexId());
        List<ProcessInstance> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowProcess::new), query.count());
    }

    public PaginList<FlowProcess> processQueryList(FlowDefinition definition) {
        ProcessInstanceQuery query = processEngine.getRuntimeService().createProcessInstanceQuery();
        query.processInstanceTenantId(definition.getTenantId());
        List<ProcessInstance> beans = query.processDefinitionId(definition.getId()).list();
        return PaginList.copyOf(Lists.transform(beans, FlowProcess::new), query.count());
    }

    public FlowProcess processQuery(Access access, String id) {
        ProcessInstanceQuery query = processEngine.getRuntimeService().createProcessInstanceQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.processInstanceTenantId(access.getDomainHexId());
        if (!access.match(Role.DOMAIN_ADMIN)) query.involvedUser(access.getHexId());
        ProcessInstance bean = query.processInstanceId(id).singleResult();
        return Optional.ofNullable(bean).map(FlowProcess::new).orElse(null);
    }

    public void processDelete(FlowProcess process, String reason) {
        processEngine.getRuntimeService().deleteProcessInstance(process.getId(), reason);
    }

    public FlowProcess.Starter processStarting(FlowDefinition definition) {
        return new FlowProcess.Starter(definition, processEngine.getRuntimeService());
    }

    // ------------------------------------------------------------------------

    public FlowExecution executionQuery(Access access, FlowProcess process, String activityKey) {
        Preconditions.checkState(access.match(Role.SYSTEM_ADMIN, Role.DOMAIN_ADMIN), "Invalid access");
        ExecutionQuery query = processEngine.getRuntimeService().createExecutionQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.executionTenantId(access.getDomainHexId());
        Execution bean = query.processInstanceId(process.getId()).activityId(activityKey).singleResult();
        return Optional.ofNullable(bean).map(FlowExecution::new).orElse(null);
    }

    public void executionEventReceivedMessage(String name, String executionId, Map<String, Object> variables) {
        processEngine.getRuntimeService().trigger(executionId, variables);
    }

    public void executionEventReceivedSignal(String name, String executionId, Map<String, Object> variables) {
        processEngine.getRuntimeService().signalEventReceived(name, executionId, variables);
    }

    public void executionTrigger(String executionId, Map<String, Object> variables) {
        processEngine.getRuntimeService().trigger(executionId, variables);
    }

    // ------------------------------------------------------------------------

    public List<String> waitingIdQueryList(FlowProcess process) {
        return processEngine.getRuntimeService().getActiveActivityIds(process.getId());
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowTask> taskQueryList(Access access, QueryParams params) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.taskTenantId(access.getDomainHexId());
        if (!access.match(Role.DOMAIN_ADMIN)) query.taskCandidateOrAssigned(access.getHexId());
        if (params.containsKey(KEYWORD_KEY)) query.taskDefinitionKey(params.get(KEYWORD_KEY));
        List<Task> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowTask::new), query.count());
    }

    public PaginList<FlowTask> taskQueryList(Access access, QueryParams params, FlowProcess process) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.taskTenantId(access.getDomainHexId());
        if (!access.match(Role.DOMAIN_ADMIN)) query.taskCandidateOrAssigned(access.getHexId());
        if (params.containsKey(KEYWORD_KEY)) query.taskDefinitionKey(params.get(KEYWORD_KEY));
        List<Task> beans = query.processInstanceId(process.getId()).listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowTask::new), query.count());
    }

    public FlowTask taskQueryByKey(Access access, FlowProcess process, String key) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.taskTenantId(access.getDomainHexId());
        if (!access.match(Role.DOMAIN_ADMIN)) query.taskCandidateOrAssigned(access.getHexId());
        Task bean = query.processInstanceId(process.getId()).taskDefinitionKey(key).singleResult();
        return Optional.ofNullable(bean).map(FlowTask::new).orElse(null);
    }

    public FlowTask taskQueryById(Access access, String id) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.taskTenantId(access.getDomainHexId()).active();
        if (!access.match(Role.DOMAIN_ADMIN)) query.taskCandidateOrAssigned(access.getHexId());
        Task bean = query.taskId(id).singleResult();
        return Optional.ofNullable(bean).map(FlowTask::new).orElse(null);
    }

    public void taskDelete(FlowTask task) {
        processEngine.getTaskService().deleteTask(task.getId(), true);
    }

    public FlowTask.Submitter taskCompleting(FlowTask task) {
        return new FlowTask.Submitter(task, processEngine.getTaskService());
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowHistoricActivity> historicActivitieQueryList(Access access, QueryParams params) {
        HistoricActivityInstanceQuery query = processEngine.getHistoryService().createHistoricActivityInstanceQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.activityTenantId(access.getDomainHexId());
        if (!access.match(Role.DOMAIN_ADMIN)) query.taskAssignee(access.getHexId());
        List<HistoricActivityInstance> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowHistoricActivity::new), query.count());
    }

    public PaginList<FlowHistoricActivity> historicActivitieQueryList(Access access, QueryParams params, FlowProcess process) {
        HistoricActivityInstanceQuery query = processEngine.getHistoryService().createHistoricActivityInstanceQuery();
        if (!access.match(Role.SYSTEM_ADMIN)) query.activityTenantId(access.getDomainHexId());
        if (!access.match(Role.DOMAIN_ADMIN)) query.taskAssignee(access.getHexId());
        List<HistoricActivityInstance> beans = query.processInstanceId(process.getId()).listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowHistoricActivity::new), query.count());
    }

    // ------------------------------------------------------------------------

    public List<FlowResource> resourceQueryList(String deploymentId) {
        List<String> names = processEngine.getRepositoryService().getDeploymentResourceNames(deploymentId);
        return Lists.transform(names, n -> new FlowResource(n, deploymentId));
    }

    public InputStream streamForDiagram(FlowDefinition definition) {
        return processEngine.getRepositoryService().getProcessDiagram(definition.getId());
    }

    public InputStream streamForModel(FlowDefinition definition) {
        return processEngine.getRepositoryService().getProcessModel(definition.getId());
    }

    public InputStream streamForResource(String deploymentId, String name) {
        return processEngine.getRepositoryService().getResourceAsStream(deploymentId, name);
    }

    // ------------------------------------------------------------------------

    public Map<String, Object> variableQueryMap(FlowProcess process) {
        return processEngine.getRuntimeService().getVariables(process.getId());
    }

    public Map<String, Object> variableQueryMap(FlowTask task) {
        return processEngine.getTaskService().getVariables(task.getId());
    }

    public <T> T variableQuery(FlowTask task, String key, Class<T> type) {
        return processEngine.getTaskService().getVariable(task.getId(), key, type);
    }

    // ------------------------------------------------------------------------

    public void submitEventListener(FlowableEventListener listener) {
        processEngine.getRuntimeService().addEventListener(listener);
    }

    public void removeEventListener(FlowableEventListener listener) {
        processEngine.getRuntimeService().removeEventListener(listener);
    }

}
