/*
 * Copyright 2025 Foliage Develop Team.
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

import page.foliage.guava.common.base.Preconditions;
import page.foliage.ldap.Access;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public interface Flowable {

    // ------------------------------------------------------------------------

    Long getId();

    String getProcessId();

    String getProcessDefinition();

    String getProcessActivity();

    // ------------------------------------------------------------------------

    boolean isProcessActive();

    // ------------------------------------------------------------------------

    public static class Context<T extends Flowable> {

        public final static String FLOW_NAME_PATTERN_DEFAULT = "%s：%s";

        private T bean;

        private Access access;

        public Context(T bean, Access access) {
            this.bean = bean;
            this.access = access;
        }

        public FlowDefinition definition() {
            Preconditions.checkNotNull(bean.getProcessDefinition(), "Invalid processDefinition");
            return Preconditions.checkNotNull(FlowDefinition.latest(access, bean.getProcessDefinition()), "Invalid processDefinition");
        }

        public FlowProcess process() {
            Preconditions.checkNotNull(bean.getProcessId(), "Invalid processId");
            return Preconditions.checkNotNull(FlowProcess.get(access, bean.getProcessId()), "Invalid process");
        }

        public FlowExecution execution() {
            Preconditions.checkNotNull(bean.getProcessActivity(), "Invalid processActivity");
            return Preconditions.checkNotNull(process().execution(access, bean.getProcessActivity()), "Invalid execution");
        }

        public FlowTask task() {
            Preconditions.checkNotNull(bean.getProcessActivity(), "Invalid processActivity");
            return Preconditions.checkNotNull(process().task(access, bean.getProcessActivity()), "Invalid task");
        }

        public FlowProcess.Starter starter() {
            FlowDefinition definition = definition();
            FlowProcess.Starter starter = definition.starter();
            starter.name(String.format(FLOW_NAME_PATTERN_DEFAULT, definition.getName(), bean.getId()));
            return starter.referenceId(bean.getId()).referenceType(bean.getClass().getName());
        }

        public FlowTask.Submitter submitter() {
            FlowTask.Submitter submitter = task().submitter();
            return submitter.referenceId(bean.getId()).referenceType(bean.getClass().getName());
        }

    }

}
