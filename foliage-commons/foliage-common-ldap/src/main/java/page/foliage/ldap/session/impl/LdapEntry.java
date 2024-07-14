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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

import page.foliage.guava.common.collect.Iterables;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class LdapEntry {

    // ------------------------------------------------------------------------

    private final SearchResultEntry entry;

    // ------------------------------------------------------------------------

    private LdapEntry(SearchResultEntry entry) {
        this.entry = entry;
    }

    // ------------------------------------------------------------------------

    public static LdapEntry of(SearchResultEntry entry) {
        return new LdapEntry(entry);
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
