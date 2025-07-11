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

import static page.foliage.ldap.session.IdentitySessionFactory.openSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import page.foliage.common.collect.QueryParams;
import page.foliage.guava.common.collect.ImmutableSet;
import page.foliage.ldap.session.IdentitySession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class StatelessAccess implements Access {

    // ------------------------------------------------------------------------

    private User user;

    private Domain domain;

    private Set<Role> roles;

    // ------------------------------------------------------------------------

    public StatelessAccess() {}

    // ------------------------------------------------------------------------

    public static StatelessAccess fromEmail(String email) {
        try (IdentitySession session = openSession()) {
            StatelessAccess bean = new StatelessAccess();
            bean.user = session.userSelectByEmail(email);
            bean.domain = session.domainSelectById(bean.user.getDomainId());
            bean.roles = ImmutableSet.copyOf(session.rolesSelectByParamsAndUserId(QueryParams.ALL, bean.user.getId()));
            return bean;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static StatelessAccess create(Domain domain) {
        StatelessAccess bean = new StatelessAccess();
        bean.domain = domain;
        bean.roles = ImmutableSet.of();
        return bean;
    }

    // ------------------------------------------------------------------------

    public LocalDate currentDate() {
        return LocalDate.now();
    }

    public LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }

    // ------------------------------------------------------------------------

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

}
