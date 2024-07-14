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
package page.foliage.ldap.session.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import page.foliage.guava.common.collect.Iterators;
import page.foliage.guava.common.collect.Lists;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class LdapConnection implements AutoCloseable {

    // ------------------------------------------------------------------------

    private final static Logger LOGGER = LoggerFactory.getLogger(LdapConnection.class);

    private final LDAPConnection connection;

    private final String base;

    // ------------------------------------------------------------------------

    public LdapConnection(String base, LDAPConnection connection) {
        this.base = base;
        this.connection = connection;
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        connection.close();
    }

    // ------------------------------------------------------------------------

    public LdapEntry selectOne(String filter) throws LDAPException {
        return selectOne(base, filter);
    }

    public LdapEntry selectOne(String point, String filter) throws LDAPException {
        SearchRequest request = new SearchRequest(point, SearchScope.SUB, filter);
        LOGGER.debug("LDAP select one: {}", request.toString());
        return LdapEntry.of(connection.searchForEntry(request));
    }

    // ------------------------------------------------------------------------

    public Iterator<LdapEntry> selectTree(Pattern pattern) throws LDAPException {
        SearchRequest request = new SearchRequest(base, SearchScope.SUB, "(objectClass=*)");
        LOGGER.debug("LDAP select tree: {}", request.toString());
        List<SearchResultEntry> result = connection.search(request).getSearchEntries();
        List<LdapEntry> entries = Lists.newArrayListWithExpectedSize(result.size());
        for (SearchResultEntry item : result) {
            Matcher matcher = pattern.matcher(item.getDN());
            if (matcher.find()) entries.add(LdapEntry.of(item));
        }
        return entries.iterator();
    }

    public Iterator<LdapEntry> selectTree(String filter) throws LDAPException {
        return selectTree(base, filter);
    }

    public Iterator<LdapEntry> selectTree(DN dn, String filter) throws LDAPException {
        return selectTree(dn.toString(), filter);
    }

    public Iterator<LdapEntry> selectTree(RDN rdn, String filter) throws LDAPException {
        return selectTree(rdn, new DN(base), filter);
    }

    public Iterator<LdapEntry> selectTree(RDN rdn, DN parent, String filter) throws LDAPException {
        return selectTree(new DN(rdn, parent).toString(), filter);
    }

    public Iterator<LdapEntry> selectTree(String point, String filter) throws LDAPException {
        SearchRequest request = new SearchRequest(point, SearchScope.SUB, filter);
        LOGGER.debug("LDAP select tree: {}", request.toString());
        return Iterators.transform(connection.search(request).getSearchEntries().iterator(), LdapEntry::of);
    }

    // ------------------------------------------------------------------------

    public LDAPResult insert(DN dn, Collection<Attribute> attributes) throws LDAPException {
        AddRequest request = new AddRequest(dn, attributes);
        LOGGER.debug("LDAP insert request: {}", request.toString());
        return connection.add(request);
    }

    public LDAPResult delete(DN dn) throws LDAPException {
        DeleteRequest request = new DeleteRequest(dn);
        LOGGER.debug("LDAP delete request: {}", request);
        return connection.delete(request);
    }

    // ------------------------------------------------------------------------

    public LdapEntry successor(String name, Object value) throws LDAPException {
        return successor(base, name, value);
    }

    public LdapEntry successor(String point, String name, Object value) throws LDAPException {
        DN dn = new DN(new RDN(name, value.toString()), new DN(point));
        LOGGER.debug("LDAP successor: {}", dn.toString());
        return LdapEntry.of(connection.getEntry(dn.toString()));
    }

    // ------------------------------------------------------------------------

    public LdapEntry reverse(LdapEntry entry, int level) throws LDAPException {
        String name = entry.getParentName(level);
        LOGGER.debug("LDAP reverse: {}", name);
        return LdapEntry.of(connection.getEntry(name));
    }

}
