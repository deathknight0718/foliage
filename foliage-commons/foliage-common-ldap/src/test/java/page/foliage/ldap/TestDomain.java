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
package page.foliage.ldap;

import org.slf4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;

import page.foliage.common.collect.QueryParams;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.common.ioc.InstanceGuice;
import page.foliage.inject.AbstractModule;
import page.foliage.inject.Provides;
import page.foliage.ldap.session.IdentitySession;
import page.foliage.ldap.session.LdapConnection;
import page.foliage.ldap.session.impl.IdentitySessionLdap;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class TestDomain {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TestDomain.class);

    private static final String LDAP_SEARCH_BASE = "dc=cecdat,dc=com";

    // ------------------------------------------------------------------------

    private static final String LDAP_PROVIDER_URL = "portal.cecdat.dev";

    private static final String LDAP_SECURITY_PRINCIPAL = "cn=admin,dc=cecdat,dc=com";

    private static final String LDAP_SECURITY_CREDENTIALS = "changeit";

    private static final LDAPConnectionOptions OPTIONS = new LDAPConnectionOptions();

    static {
        OPTIONS.setFollowReferrals(false);
    }

    // ------------------------------------------------------------------------

    @BeforeClass
    public static void beforeClass() {
        InstanceFactory.provide(InstanceGuice.withModule(new InternalModule()));
    }

    @Test(enabled = true)
    public void testDomainList() throws Exception {
        for (Domain domain : Domain.list(QueryParams.ALL)) {
            LOGGER.info("Domain: {} {} {}", domain.getId(), domain.getIdentifier(), domain.getDisplayName());
        }
    }

    @Test(enabled = true)
    public void testDomainTree() throws Exception {
        for (Domain domain : Domain.get("domain1").members(QueryParams.ALL)) {
            LOGGER.info("Domain: {} {} {}", domain.getId(), domain.getIdentifier(), domain.getDisplayName());
        }
    }

    @Test(enabled = true)
    public void testDomainTree2() throws Exception {
        for (Domain domain : Domain.get("domain1s1").members(QueryParams.ALL)) {
            LOGGER.info("Domain: {} {} {}", domain.getId(), domain.getIdentifier(), domain.getDisplayName());
        }
    }

    @Test(enabled = true)
    public void testDomainUserList() throws Exception {
        Domain domain = Domain.get("domain1");
        for (User user : domain.users(QueryParams.ALL)) {
            LOGGER.info("User: {} {} {} {}", user.getId(), user.getName(), user.getEmail(), user.domain().getDisplayName());
        }
    }

     @Test(enabled = true)
    public void testDomainParent() throws Exception {
        Domain domain = Domain.get("domain1s1");
        Domain parent = domain.parent();
        LOGGER.info("Parent Domain: {} {} {}", parent.getId(), parent.getIdentifier(), parent.getDisplayName());
    }

    // ------------------------------------------------------------------------

    private static class InternalModule extends AbstractModule {

        @Provides
        public IdentitySession buildSession() throws Exception {
            return new IdentitySessionLdap(buildConnection());
        }

        @Provides
        public LdapConnection buildConnection() throws Exception {
            return new LdapConnection(LDAP_SEARCH_BASE, new LDAPConnection(OPTIONS, LDAP_PROVIDER_URL, 1389, LDAP_SECURITY_PRINCIPAL, LDAP_SECURITY_CREDENTIALS));
        }

    }

}
