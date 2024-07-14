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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.jackson.LocalDateTimeSerializer;
import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.base.Preconditions;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FormPayloadReference {

    // ------------------------------------------------------------------------

    private final Long id;

    private String key, processId, executionId;

    private LocalDateTime updateTime;

    // ------------------------------------------------------------------------

    FormPayloadReference(Long id) {
        this.id = id;
    }

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    public static FormPayloadReference get(Long id) {
        try (FederatedSession session = singleton().openSession()) {
            return session.referenceSelectById(id);
        } catch (SQLException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    public FormPayload source() {
        try (FederatedSession session = singleton().openSession()) {
            return session.payloadSelectById(id);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        FormPayloadReference rhs = (FormPayloadReference) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) //
            .append(id).append(key) //
            .append(processId).append(executionId) //
            .append(updateTime) //
            .build();
    }

    // ------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        String key, processId, executionId;

        FormPayload source;

        Builder() {}

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder executionId(String executionId) {
            this.executionId = executionId;
            return this;
        }

        public Builder source(FormPayload source) {
            this.source = source;
            return this;
        }

        public FormPayloadReference build(String processId) {
            try (FederatedSession session = singleton().openSession()) {
                this.processId = Preconditions.checkNotNull(processId);
                Preconditions.checkNotNull(key);
                Preconditions.checkNotNull(source);
                return session.referenceInsertByBuilder(this);
            } catch (SQLException | IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

}
