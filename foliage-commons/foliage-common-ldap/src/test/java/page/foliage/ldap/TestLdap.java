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

import java.time.LocalDateTime;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;

import page.foliage.common.ioc.InstanceFactory;
import page.foliage.common.ioc.InstanceGuice;
import page.foliage.inject.AbstractModule;
import page.foliage.inject.Provides;
import page.foliage.ldap.session.IdentitySession;
import page.foliage.ldap.session.impl.LdapConnection;
import page.foliage.ldap.session.impl.LdapIdentitySession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class TestLdap {

    // ------------------------------------------------------------------------

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
    private static void beforeClass() {
        InstanceFactory.provide(InstanceGuice.withModule(new InternalModule()));
    }

    @Test
    private void testContractBuild() throws Exception {
        Contract.Builder builder = Contract.builder(StatelessAccess.fromEmail("domain1admin1@cecdat.com"));
        builder.name("repo1contract2").domainId(800003L).repositoryId(800002001L).expiration(LocalDateTime.now().plusYears(1));
        Contract contract = builder.build();
        contract.remove();
    }

    // ------------------------------------------------------------------------

    private static class InternalModule extends AbstractModule {

        @Provides
        public IdentitySession buildSession() throws Exception {
            return new LdapIdentitySession(buildConnection());
        }

        @Provides
        public LdapConnection buildConnection() throws Exception {
            return new LdapConnection(LDAP_SEARCH_BASE, new LDAPConnection(OPTIONS, LDAP_PROVIDER_URL, 1389, LDAP_SECURITY_PRINCIPAL, LDAP_SECURITY_CREDENTIALS));
        }

    }

}
