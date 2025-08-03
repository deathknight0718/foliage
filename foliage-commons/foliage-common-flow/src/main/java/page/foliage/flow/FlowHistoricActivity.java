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

import org.flowable.engine.history.HistoricActivityInstance;

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
public class FlowHistoricActivity {

    // ------------------------------------------------------------------------

    private final HistoricActivityInstance delegate;

    // ------------------------------------------------------------------------

    public FlowHistoricActivity(HistoricActivityInstance delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    // ------------------------------------------------------------------------

    public static PaginList<FlowHistoricActivity> list(Access access, QueryParams params) {
        return getInstance(FederatedEngine.class).historicActivitieQueryList(access, params);
    }

    // ------------------------------------------------------------------------

    public String getId() {
        return delegate.getId();
    }

    public String getTenantId() {
        return delegate.getTenantId();
    }

    public String getActivityId() {
        return delegate.getActivityId();
    }

    public String getActivityName() {
        return delegate.getActivityName();
    }

    public String getActivityType() {
        return delegate.getActivityType();
    }

    public String getProcessId() {
        return delegate.getProcessInstanceId();
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getStartTime() {
        return DateTimes.toLocalDateTime(delegate.getStartTime());
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getEndTime() {
        return delegate.getEndTime() == null ? null : DateTimes.toLocalDateTime(delegate.getEndTime());
    }

}
