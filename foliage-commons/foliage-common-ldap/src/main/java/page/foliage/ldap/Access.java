/*
 * Copyright 2024 Foliage Develop Team.
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
package page.foliage.ldap;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import page.foliage.common.collect.QueryParams;
import page.foliage.common.jackson.Hex36Serializer;
import page.foliage.common.util.CodecUtils;
import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.collect.ImmutableSet;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Access implements Serializable {

    // ------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    private static final ThreadLocal<Access> THREAD_LOCAL = ThreadLocal.withInitial(() -> null);

    // ------------------------------------------------------------------------

    private User user;

    private Domain domain;

    private Set<Role> roles;

    // ------------------------------------------------------------------------

    private Access() {}

    // ------------------------------------------------------------------------

    public static Access get(Long id) {
        return from(User.get(id));
    }

    public static Access fromEmail(String email) {
        return from(User.fromEmail(email));
    }

    public static Access from(User user) {
        Access bean = new Access();
        bean.user = user;
        bean.domain = user.domain();
        bean.roles = ImmutableSet.copyOf(user.roles(QueryParams.ALL));
        return bean;
    }

    // ------------------------------------------------------------------------

    public static Access current() {
        return THREAD_LOCAL.get();
    }

    public static void register(Long id) {
        THREAD_LOCAL.set(get(id));
    }

    public static void register(String email) {
        THREAD_LOCAL.set(fromEmail(email));
    }

    public static void register(Access value) {
        THREAD_LOCAL.set(value);
    }

    public static void unregister() {
        THREAD_LOCAL.remove();
    }

    public static boolean isRegistered() {
        return current() != null;
    }

    public static <T> T withTemporary(Access current, Supplier<T> operation) {
        Access original = current();
        try {
            register(current);
            return operation.get();
        } finally {
            if (original != null) register(original);
            else unregister();
        }
    }

    // ------------------------------------------------------------------------

    public boolean match(Role... roles) {
        if (this.roles.contains(Role.SYSTEM_ADMIN)) return true;
        if (roles == null || roles.length == 0) return false;
        ImmutableSet<Role> checks = ImmutableSet.copyOf(roles);
        return this.roles.stream().anyMatch(checks::contains);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        Domain rhs = (Domain) obj;
        return new EqualsBuilder().append(getId(), rhs.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    // ------------------------------------------------------------------------

    public LocalDate currentDate() {
        return LocalDate.now();
    }

    public LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }

    // ------------------------------------------------------------------------

    @JsonSerialize(using = Hex36Serializer.class)
    public Long getId() {
        return user.getId();
    }

    public String getHexId() {
        return CodecUtils.encodeHex36(getId());
    }

    @JsonSerialize(using = Hex36Serializer.class)
    public Long getDomainId() {
        return domain.getId();
    }

    public String getDomainHexId() {
        return CodecUtils.encodeHex36(getDomainId());
    }

    public User getUser() {
        return user;
    }

    public Domain getDomain() {
        return domain;
    }

    public Set<Role> getRoles() {
        return roles;
    }

}
