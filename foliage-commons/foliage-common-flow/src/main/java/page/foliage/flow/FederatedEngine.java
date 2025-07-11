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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskCompletionBuilder;
import org.flowable.task.api.TaskQuery;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.ldap.Domain;
import page.foliage.ldap.User;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.Lists;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FederatedEngine {

    // ------------------------------------------------------------------------

    public final static String KEYWORD_KEY = "key";

    // ------------------------------------------------------------------------

    private final DataSource source;

    private final ProcessEngine processEngine;

    private final String schema;

    // ------------------------------------------------------------------------

    public FederatedEngine(DataSource source, String schema, ProcessEngine processEngine) {
        this.source = Preconditions.checkNotNull(source);
        this.schema = Preconditions.checkNotNull(schema);
        this.processEngine = processEngine;
    }

    // ------------------------------------------------------------------------

    public static FederatedEngine singleton() {
        return InstanceFactory.getInstance(FederatedEngine.class);
    }

    // ------------------------------------------------------------------------

    public FederatedSession openSession() throws SQLException {
        return new FederatedSession(source.getConnection(), schema);
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowDeployment> deploymentsQueryByParamsAndDomain(QueryParams params, Domain domain) {
        DeploymentQuery query = processEngine.getRepositoryService().createDeploymentQuery();
        query.deploymentTenantId(domain.getIdentifier());
        if (params.containsKey(KEYWORD_KEY)) query.deploymentKey(params.get(KEYWORD_KEY));
        List<Deployment> deployments = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(deployments, FlowDeployment::new), query.count());
    }

    public FlowDeployment deploymentQueryById(String id) {
        Deployment bean = processEngine.getRepositoryService().createDeploymentQuery().deploymentId(id).singleResult();
        return Optional.ofNullable(bean).map(FlowDeployment::new).orElse(null);
    }

    public FlowDeployment deploymentQueryByKeyAndDomain(String key, Domain domain) {
        DeploymentQuery query = processEngine.getRepositoryService().createDeploymentQuery();
        query.deploymentTenantId(domain.getIdentifier());
        Deployment bean = query.deploymentKey(key).latest().singleResult();
        return Optional.ofNullable(bean).map(FlowDeployment::new).orElse(null);
    }

    public FlowDeployment.Builder deploymentBuilder(Domain domain) {
        DeploymentBuilder builder = processEngine.getRepositoryService().createDeployment();
        builder.tenantId(domain.getIdentifier());
        return new FlowDeployment.Builder(builder);
    }

    public void deploymentDeleteById(String id) {
        processEngine.getRepositoryService().deleteDeployment(id, true);
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowDefinition> definitionsQueryByParamsAndDomain(QueryParams params, Domain domain) {
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        query.processDefinitionTenantId(domain.getIdentifier());
        if (params.containsKey(KEYWORD_KEY)) query.processDefinitionKey(params.get(KEYWORD_KEY));
        List<ProcessDefinition> definitions = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(definitions, FlowDefinition::new), query.count());
    }

    public PaginList<FlowDefinition> definitionsQueryByParamsAndDeploymentId(QueryParams params, String deploymentId) {
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        query.deploymentId(deploymentId);
        if (params.containsKey(KEYWORD_KEY)) query.processDefinitionKey(params.get(KEYWORD_KEY));
        List<ProcessDefinition> definitions = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(definitions, FlowDefinition::new), query.count());
    }

    public FlowDefinition definitionQueryById(String id) {
        ProcessDefinition bean = processEngine.getRepositoryService().getProcessDefinition(id);
        return Optional.ofNullable(bean).map(FlowDefinition::new).orElse(null);
    }

    public FlowDefinition definitionQueryByKeyAndDomain(String key, Domain domain) {
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        query.processDefinitionTenantId(domain.getIdentifier());
        ProcessDefinition bean = query.processDefinitionKey(key).latestVersion().singleResult();
        return Optional.ofNullable(bean).map(FlowDefinition::new).orElse(null);
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowProcess> processesQueryByParamsAndDomain(QueryParams params, Domain domain) {
        ProcessInstanceQuery query = processEngine.getRuntimeService().createProcessInstanceQuery();
        query.processInstanceTenantId(domain.getIdentifier());
        List<ProcessInstance> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowProcess::new), query.count());
    }

    public PaginList<FlowProcess> processesQueryByParamsAndUser(QueryParams params, User user) {
        ProcessInstanceQuery query = processEngine.getRuntimeService().createProcessInstanceQuery();
        query.processInstanceTenantId(user.domain().getIdentifier());
        query.involvedUser(user.getId().toString());
        List<ProcessInstance> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowProcess::new), query.count());
    }

    public PaginList<FlowProcess> processesQueryByParamsAndDefinitionId(QueryParams params, String definitionId) {
        ProcessInstanceQuery query = processEngine.getRuntimeService().createProcessInstanceQuery();
        query.processDefinitionId(definitionId);
        List<ProcessInstance> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowProcess::new), query.count());
    }

    public FlowProcess processQueryById(String id) {
        ProcessInstance bean = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(id).singleResult();
        return Optional.ofNullable(bean).map(FlowProcess::new).orElse(null);
    }

    public FlowProcess.Builder processBuild(Domain domain) {
        ProcessInstanceBuilder builder = processEngine.getRuntimeService().createProcessInstanceBuilder();
        builder.tenantId(domain.getIdentifier());
        return new FlowProcess.Builder(builder);
    }

    public FlowProcess.Builder processBuild(User user) {
        processEngine.getIdentityService().setAuthenticatedUserId(user.getId().toString());
        ProcessInstanceBuilder builder = processEngine.getRuntimeService().createProcessInstanceBuilder();
        builder.tenantId(user.domain().getIdentifier());
        return new FlowProcess.Builder(builder);
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowTask> tasksQueryByParamsAndDomain(QueryParams params, Domain domain) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        query.taskTenantId(domain.getIdentifier());
        List<Task> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowTask::new), query.count());
    }

    public PaginList<FlowTask> tasksQueryByParamsAndUser(QueryParams params, User user) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        query.taskTenantId(user.domain().getIdentifier());
        query.taskCandidateOrAssigned(user.getId().toString());
        List<Task> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowTask::new), query.count());
    }

    public PaginList<FlowTask> tasksQueryByParamsAndProcessId(QueryParams params, String processId) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        query.processInstanceId(processId);
        List<Task> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowTask::new), query.count());
    }

    public FlowTask taskQueryById(String id) {
        Task bean = processEngine.getTaskService().createTaskQuery().taskId(id).singleResult();
        return Optional.ofNullable(bean).map(FlowTask::new).orElse(null);
    }

    public FlowTask.SubmitionBuilder taskCompletionBuild(User user, String processId, String taskId, String formKey) {
        processEngine.getIdentityService().setAuthenticatedUserId(user.getId().toString());
        TaskCompletionBuilder builder = processEngine.getTaskService().createTaskCompletionBuilder();
        builder.taskId(taskId);
        return new FlowTask.SubmitionBuilder(builder).formKey(formKey).taskId(taskId).processId(processId);
    }

    public void taskDeleteById(String id) {
        processEngine.getTaskService().deleteTask(id, true);
    }

    // ------------------------------------------------------------------------

    public PaginList<FlowHistoricActivity> historicActivitiesQueryByParamsAndDomain(QueryParams params, Domain domain) {
        HistoricActivityInstanceQuery query = processEngine.getHistoryService().createHistoricActivityInstanceQuery();
        query.activityTenantId(domain.getIdentifier());
        List<HistoricActivityInstance> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowHistoricActivity::new), query.count());
    }

    public PaginList<FlowHistoricActivity> historicActivitiesQueryByParamsAndProcessId(QueryParams params, String processId) {
        HistoricActivityInstanceQuery query = processEngine.getHistoryService().createHistoricActivityInstanceQuery();
        query.processInstanceId(processId);
        List<HistoricActivityInstance> beans = query.listPage(params.offset(), params.limit());
        return PaginList.copyOf(Lists.transform(beans, FlowHistoricActivity::new), query.count());
    }

    // ------------------------------------------------------------------------

    public List<FlowResource> resourcesQueryByDeploymentId(String deploymentId) {
        List<String> names = processEngine.getRepositoryService().getDeploymentResourceNames(deploymentId);
        return Lists.transform(names, n -> new FlowResource(n, deploymentId));
    }

    public InputStream streamForDiagramByDefinitionId(String definitionId) {
        return processEngine.getRepositoryService().getProcessDiagram(definitionId);
    }

    public InputStream streamForModelByDefinitionId(String definitionId) {
        return processEngine.getRepositoryService().getProcessModel(definitionId);
    }

    public InputStream streamForResourceByDeploymentIdAndName(String deploymentId, String name) {
        return processEngine.getRepositoryService().getResourceAsStream(deploymentId, name);
    }

    // ------------------------------------------------------------------------

    public String formKeyQueryByDefinitionId(String definitionId) {
        return processEngine.getFormService().getStartFormKey(definitionId);
    }

    public String formKeyQueryByDefinitionIdAndTaskKey(String definitionId, String taskKey) {
        return processEngine.getFormService().getTaskFormKey(definitionId, taskKey);
    }

    // ------------------------------------------------------------------------

    public FlowVariables variablesQueryByProcessId(String processId) {
        return new FlowVariables(processEngine.getRuntimeService().getVariables(processId));
    }

    public FlowVariables variablesQueryByTaskId(String taskId) {
        return new FlowVariables(processEngine.getTaskService().getVariables(taskId));
    }

    // ------------------------------------------------------------------------

    public void submitEventListener(FlowableEventListener listener) {
        processEngine.getRuntimeService().addEventListener(listener);
    }

    public void removeEventListener(FlowableEventListener listener) {
        processEngine.getRuntimeService().removeEventListener(listener);
    }

    public void message(String name, String executionId, Map<String, Object> processVariables) {
        processEngine.getRuntimeService().messageEventReceived(name, executionId, processVariables);
    }

}
