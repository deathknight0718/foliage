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
import page.foliage.ldap.session.IdentitySessionLdap;
import page.foliage.ldap.session.LdapConnection;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class TestLdap {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TestLdap.class);

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

    @Test(enabled = false)
    public void testRepositoryContractBuild() throws Exception {
        Contract.Builder builder = Contract.builder(StatelessAccess.fromEmail("domain2admin1@cecdat.com"));
        builder.name("repo1contract2").domainId(800003L).repositoryId(800002001L).expiration(LocalDateTime.now().plusYears(1));
        Contract contract = builder.build();
        contract.remove();
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
    public void testUserList() throws Exception {
        Domain domain = Domain.get("domain1");
        for (User user : domain.users(QueryParams.ALL)) {
            LOGGER.info("User: {} {} {}", user.getId(), user.getName(), user.getEmail());
        }
    }

    @Test(enabled = true)
    public void testRepositoryList() throws Exception {
        Domain domain = Domain.get("system");
        for (Repository repository : domain.repositories(QueryParams.ALL)) {
            LOGGER.info("Repository: {} {} {}", repository.getId(), repository.getName(), repository.getDisplayName());
        }
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
