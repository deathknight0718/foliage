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
package page.foliage.ldap.session;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.RDN;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.guava.common.collect.Lists;
import page.foliage.ldap.Dashboard;
import page.foliage.ldap.Dashboard.Builder;
import page.foliage.ldap.Domain;
import page.foliage.ldap.Role;
import page.foliage.ldap.User;
import page.foliage.ldap.session.LdapConnection.Entry;

/**
 * @author deathknight0718@qq.com
 */
public class IdentitySessionLdap implements IdentitySession {

    // ------------------------------------------------------------------------

    private final LdapConnection connection;

    // ------------------------------------------------------------------------

    public IdentitySessionLdap(LdapConnection connection) {
        this.connection = connection;
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        connection.close();
    }

    // ------------------------------------------------------------------------

    @Override
    public PaginList<Domain> domainsSelectByParams(QueryParams params) throws Exception {
        List<Domain> beans = Lists.newArrayList();
        Iterator<Entry> iterator = connection.selectTree("(&(dc=*)(uniqueIdentifier=*))");
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            Long id = entry.get("uniqueIdentifier").asLong();
            Domain bean = new Domain(id, entry.get("dc").asText());
            bean.setDisplayName(entry.get("displayName").asText());
            beans.add(bean);
        }
        Stream<Domain> stream = beans.stream();
        stream = stream.skip(params.offset()).limit(params.limit());
        return PaginList.copyOf(stream.collect(Collectors.toList()), beans.size());
    }

    @Override
    public PaginList<Domain> domainsRecursionByParamsAndRoot(QueryParams params, Domain root) throws Exception {
        List<Domain> beans = Lists.newArrayList();
        Iterator<Entry> iterator = connection.selectTree(new RDN("dc", root.getIdentifier()), "(&(dc=*)(uniqueIdentifier=*))");
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            Long id = entry.get("uniqueIdentifier").asLong();
            Domain bean = new Domain(id, entry.get("dc").asText());
            bean.setDisplayName(entry.get("displayName").asText());
            beans.add(bean);
        }
        Stream<Domain> stream = beans.stream();
        stream = stream.skip(params.offset()).limit(params.limit());
        return PaginList.copyOf(stream.collect(Collectors.toList()), beans.size());
    }

    @Override
    public Domain domainSelectById(Long id) throws LDAPException {
        String filter = MessageFormat.format("(uniqueIdentifier={0,number,#})", id);
        Entry entry = connection.selectOne(filter);
        Domain bean = new Domain(entry.get("uniqueIdentifier").asLong(), entry.get("dc").asText());
        bean.setDisplayName(entry.get("displayName").asText());
        return bean;
    }

    @Override
    public Domain domainSelectByIdentifier(String identifier) throws LDAPException {
        String filter = MessageFormat.format("(dc={0})", identifier);
        Entry entry = connection.selectOne(filter);
        Domain bean = new Domain(entry.get("uniqueIdentifier").asLong(), entry.get("dc").asText());
        bean.setDisplayName(entry.get("displayName").asText());
        return bean;
    }

    // ------------------------------------------------------------------------

    @Override
    public PaginList<User> usersSelectByParamsAndDomainId(QueryParams params, Long domainId) throws LDAPException {
        List<User> beans = Lists.newArrayList();
        Domain domain = domainSelectById(domainId);
        Iterator<Entry> iterator = connection.selectTree(new RDN("dc", domain.getIdentifier()), "(uid=*)");
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            User bean = User.create(entry.get("uid").asLong());
            bean.setDisplayName(entry.get("displayName").asText());
            bean.setEmail(entry.get("mail").asText());
            bean.setName(entry.get("cn").asText());
            bean.setDomainId(connection.reverse(entry, 2).get("uniqueIdentifier").asLong());
            beans.add(bean);
        }
        Stream<User> stream = beans.stream().skip(params.offset()).limit(params.limit());
        return PaginList.copyOf(stream.collect(Collectors.toList()), beans.size());
    }

    @Override
    public User userSelectById(Long id) throws LDAPException {
        String filter = MessageFormat.format("(uid={0,number,#})", id);
        Entry entry = connection.selectOne(filter);
        User bean = User.create(entry.get("uid").asLong());
        bean.setDisplayName(entry.get("displayName").asText());
        bean.setEmail(entry.get("mail").asText());
        bean.setName(entry.get("cn").asText());
        bean.setDomainId(connection.reverse(entry, 2).get("uniqueIdentifier").asLong());
        return bean;
    }

    @Override
    public User userSelectByEmail(String email) throws LDAPException {
        String filter = MessageFormat.format("(mail={0})", email);
        Entry entry = connection.selectOne(filter);
        User bean = User.create(entry.get("uid").asLong());
        bean.setDisplayName(entry.get("displayName").asText());
        bean.setEmail(entry.get("mail").asText());
        bean.setName(entry.get("cn").asText());
        bean.setDomainId(connection.reverse(entry, 2).get("uniqueIdentifier").asLong());
        return bean;
    }

    @Override
    public User userSelectByName(String name) throws Exception {
        String filter = MessageFormat.format("(cn={0})", name);
        Entry entry = connection.selectOne(filter);
        User bean = User.create(entry.get("uid").asLong());
        bean.setDisplayName(entry.get("displayName").asText());
        bean.setEmail(entry.get("mail").asText());
        bean.setName(entry.get("cn").asText());
        bean.setDomainId(connection.reverse(entry, 2).get("uniqueIdentifier").asLong());
        return bean;
    }

    // ------------------------------------------------------------------------

    @Override
    public PaginList<Dashboard> dashboardsSelectByParamsAndDomainId(QueryParams params, Long domainId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Dashboard dashboardSelectById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Dashboard dashboardInsertOrUpdate(Builder builder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long dashboardDeleteById(Long id) {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------

    @Override
    public PaginList<Role> rolesSelectByParams(QueryParams params) throws Exception {
        List<Role> beans = Lists.newArrayList();
        Iterator<Entry> iterator = connection.selectTree("(&(objectClass=groupOfNames)(ou=roles))");
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            String name = entry.get("cn").asText();
            Role bean = Role.valueOf(name);
            beans.add(bean);
        }
        Stream<Role> stream = beans.stream();
        stream = stream.skip(params.offset()).limit(params.limit());
        return PaginList.copyOf(stream.collect(Collectors.toList()), beans.size());
    }

    @Override
    public PaginList<Role> rolesSelectByParamsAndUserId(QueryParams params, Long userId) throws LDAPException {
        List<Role> beans = Lists.newArrayList();
        String filter = MessageFormat.format("(uid={0,number,#})", userId);
        Entry point = connection.selectOne(filter);
        Iterator<Entry> iterator = connection.selectTree(MessageFormat.format("(&(ou=roles)(member={0}))", point.getName()));
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            String name = entry.get("cn").asText();
            Role bean = Role.valueOf(name);
            beans.add(bean);
        }
        Stream<Role> stream = beans.stream();
        stream = stream.skip(params.offset()).limit(params.limit());
        return PaginList.copyOf(stream.collect(Collectors.toList()), beans.size());
    }

}
