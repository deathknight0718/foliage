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
package page.foliage.test;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import org.testng.annotations.BeforeClass;
import page.foliage.common.ioc.InstanceClosingCheck;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.common.ioc.InstanceGuice;
import page.foliage.file.session.FileSessionFactory;
import page.foliage.file.session.impl.MinioSessionFactoryImpl;
import page.foliage.inject.AbstractModule;
import page.foliage.inject.Provides;
import page.foliage.inject.Singleton;
import page.foliage.ldap.session.IdentitySession;
import page.foliage.ldap.session.LdapConnection;
import page.foliage.ldap.session.impl.IdentitySessionLdap;

/**
 * @author deathknight0718@qq.com
 */
public abstract class TestBase {

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

    private static final String ENDPOINT = "https://file.foliage.page:7001";

    private static final String[] KEYS = new String[]{ "admin", "changeit" };

    // ------------------------------------------------------------------------

    @BeforeClass
    public static void beforeClass() {
        InstanceFactory.provide(InstanceGuice.withModule(new InternalModule()));
    }

    // ------------------------------------------------------------------------

    public static class InternalModule extends AbstractModule {

        @Provides
        @Singleton
        private FileSessionFactory buildMinioSessionFactory() {
            MinioSessionFactoryImpl bean = MinioSessionFactoryImpl.builder() //
                    .logging().endpoint(ENDPOINT).credentials(KEYS[0], KEYS[1]).build();
            return InstanceClosingCheck.hook(bean);
        }

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
