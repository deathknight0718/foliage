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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

import page.foliage.guava.common.collect.Iterables;
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

    public Entry selectOne(String filter) throws LDAPException {
        return selectOne(base, filter);
    }

    public Entry selectOne(String point, String filter) throws LDAPException {
        SearchRequest request = new SearchRequest(point, SearchScope.SUB, filter);
        LOGGER.debug("LDAP select one: {}", request.toString());
        return Entry.of(connection.searchForEntry(request));
    }

    // ------------------------------------------------------------------------

    public Iterator<Entry> selectTree(Pattern pattern) throws LDAPException {
        SearchRequest request = new SearchRequest(base, SearchScope.SUB, "(objectClass=*)");
        LOGGER.debug("LDAP select tree: {}", request.toString());
        List<SearchResultEntry> result = connection.search(request).getSearchEntries();
        List<Entry> entries = Lists.newArrayListWithExpectedSize(result.size());
        for (SearchResultEntry item : result) {
            Matcher matcher = pattern.matcher(item.getDN());
            if (matcher.find()) entries.add(Entry.of(item));
        }
        return entries.iterator();
    }

    public Iterator<Entry> selectTree(String filter) throws LDAPException {
        return selectTree(base, filter);
    }

    public Iterator<Entry> selectTree(DN dn, String filter) throws LDAPException {
        return selectTree(dn.toString(), filter);
    }

    public Iterator<Entry> selectTree(RDN rdn, String filter) throws LDAPException {
        return selectTree(rdn, new DN(base), filter);
    }

    public Iterator<Entry> selectTree(RDN rdn, DN parent, String filter) throws LDAPException {
        return selectTree(new DN(rdn, parent).toString(), filter);
    }

    public Iterator<Entry> selectTree(String point, String filter) throws LDAPException {
        SearchRequest request = new SearchRequest(point, SearchScope.SUB, filter);
        LOGGER.debug("LDAP select tree: {}", request.toString());
        return Iterators.transform(connection.search(request).getSearchEntries().iterator(), Entry::of);
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

    public Entry successor(String name, Object value) throws LDAPException {
        return successor(base, name, value);
    }

    public Entry successor(String point, String name, Object value) throws LDAPException {
        DN dn = new DN(new RDN(name, value.toString()), new DN(point));
        LOGGER.debug("LDAP successor: {}", dn.toString());
        return Entry.of(connection.getEntry(dn.toString()));
    }

    // ------------------------------------------------------------------------

    public Entry reverse(Entry entry, int level) throws LDAPException {
        String name = entry.getParentName(level);
        LOGGER.debug("LDAP reverse: {}", name);
        return Entry.of(connection.getEntry(name));
    }

    // ------------------------------------------------------------------------

    public static class Entry {

        // ------------------------------------------------------------------------

        private final SearchResultEntry entry;

        // ------------------------------------------------------------------------

        private Entry(SearchResultEntry entry) {
            this.entry = entry;
        }

        // ------------------------------------------------------------------------

        public static Entry of(SearchResultEntry entry) {
            return new Entry(entry);
        }

        // ------------------------------------------------------------------------

        public Value get(String key) {
            return new Value(entry.getAttribute(key));
        }

        public Iterable<Value> values() {
            return Iterables.transform(entry.getAttributes(), a -> new Value(a));
        }

        public String getName() {
            return entry.getDN();
        }

        public String getParentName(int level) {
            try {
                DN dn = new DN(getName());
                for (int i = 0; i < level; i++) {
                    dn = dn.getParent();
                }
                return dn.toString();
            } catch (LDAPException e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

    // ------------------------------------------------------------------------

    public static class Value {

        private final Attribute value;

        public Value(Attribute value) {
            this.value = value;
        }

        public String asKey() {
            return value.getName();
        }

        public String asText() {
            return value.getValue();
        }

        public Long asLong() {
            return value.getValueAsLong();
        }

        public LocalDateTime asTimeStamp(ZoneId zoneId) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(asLong()), zoneId);
        }

    }

}
