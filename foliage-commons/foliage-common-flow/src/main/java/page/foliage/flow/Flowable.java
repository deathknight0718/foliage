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

    default boolean isReadOnly() {
        return getProcessId() != null && !isProcessActive();
    }

    // ------------------------------------------------------------------------

    public static abstract class Updater<T extends Flowable> {

        protected final T bean;

        public Updater(T bean) {
            this.bean = bean;
        }

        protected abstract Updater<T> processId(String processId);

        protected abstract Updater<T> processActive(boolean processActive);

        public abstract T update();

    }

    // ------------------------------------------------------------------------

    public static abstract class Starter<T extends Flowable> {

        public final static String FLOW_NAME_PATTERN_DEFAULT = "%s：%s";

        private Updater<T> updater;

        public Starter(Updater<T> updater) {
            Preconditions.checkNotNull(updater.bean.getProcessDefinition(), "Invalid processDefinition");
            this.updater = updater;
        }

        public T start() {
            return start(Access.current());
        }

        public T start(Access access) {
            updater.processActive(false);
            FlowDefinition definition = Preconditions.checkNotNull(FlowDefinition.latest(access, updater.bean.getProcessDefinition()), "Invalid processDefinition");
            FlowProcess.Starter starter = definition.starter(access);
            starter.name(String.format(FLOW_NAME_PATTERN_DEFAULT, definition.getName(), updater.bean.getId()));
            starter.referenceId(updater.bean.getId());
            starter.referenceType(updater.bean.getClass().getName());
            return doStart(updater.bean, starter);
        }

        protected abstract T doStart(T bean, FlowProcess.Starter starter);

    }

    // ------------------------------------------------------------------------

    public static abstract class Submitter<T extends Flowable> {

        private final Updater<T> updater;

        public Submitter(Updater<T> updater) {
            Preconditions.checkNotNull(updater.bean.getProcessId(), "Invalid processId");
            Preconditions.checkNotNull(updater.bean.getProcessActivity(), "Invalid processActivity");
            this.updater = updater;
        }

        public T submit() {
            return submit(Access.current());
        }

        public T submit(Access access) {
            updater.processActive(false);
            FlowProcess process = Preconditions.checkNotNull(FlowProcess.get(access, updater.bean.getProcessId()), "Invalid process");
            FlowTask task = Preconditions.checkNotNull(process.task(access, updater.bean.getProcessActivity()), "Invalid task");
            FlowTask.Submitter submitter = task.submitter(access);
            submitter.referenceId(updater.bean.getId());
            submitter.referenceType(updater.bean.getClass().getName());
            return doSubmit(updater.bean, submitter);
        }

        protected abstract T doSubmit(T bean, FlowTask.Submitter submitter);

    }

    // ------------------------------------------------------------------------

    public static abstract class Executer<T extends Flowable> {

        private final Updater<T> updater;

        public Executer(Updater<T> updater, String activityKey) {
            Preconditions.checkNotNull(updater.bean.getProcessId(), "Invalid processId");
            Preconditions.checkNotNull(updater.bean.getProcessActivity(), "Invalid processActivity");
            this.updater = updater;
        }

        public T execute() {
            return execute(Access.current());
        }

        public T execute(Access access) {
            updater.processActive(false);
            FlowProcess process = Preconditions.checkNotNull(FlowProcess.get(access, updater.bean.getProcessId()), "Invalid process");
            FlowExecution execution = Preconditions.checkNotNull(process.execution(access, updater.bean.getProcessActivity()), "Invalid execution");
            return doExecute(updater.bean, execution);
        }

        protected T doExecute(T bean, FlowExecution execution) {
            return bean;
        }

    }

    // ------------------------------------------------------------------------

    public static abstract class Terminator<T extends Flowable> {

        private final Updater<T> updater;

        private String reason;

        public Terminator(Updater<T> updater) {
            this.updater = updater;
            Preconditions.checkNotNull(updater.bean.getProcessId(), "Invalid processId");
        }

        public Terminator<T> reason(String reason) {
            this.reason = reason;
            return this;
        }

        public T terminate() {
            return terminate(Access.current());
        }

        public T terminate(Access access) {
            Preconditions.checkNotNull(reason, "Invalid reason");
            updater.processActive(false);
            FlowProcess process = Preconditions.checkNotNull(FlowProcess.get(access, updater.bean.getProcessId()), "Invalid process");
            return doTerminate(updater.bean, process, reason);
        }

        protected abstract T doTerminate(T bean, FlowProcess process, String reason);

    }

}
