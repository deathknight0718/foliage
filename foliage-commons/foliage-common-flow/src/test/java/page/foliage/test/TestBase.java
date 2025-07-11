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

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.flowable.common.engine.impl.de.odysseus.el.ExpressionFactoryImpl;
import org.testng.annotations.BeforeClass;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import page.foliage.common.ioc.InstanceClosingCheck;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.common.ioc.InstanceGuice;
import page.foliage.flow.FederatedEngine;
import page.foliage.flow.FederatedEngineBuilder;
import page.foliage.flow.JuelExpressionFactory;
import page.foliage.guava.common.io.Resources;
import page.foliage.inject.AbstractModule;
import page.foliage.inject.Provides;
import page.foliage.inject.Singleton;
import page.foliage.ldap.session.IdentitySession;
import page.foliage.ldap.session.IdentitySessionLdap;
import page.foliage.ldap.session.LdapConnection;

/**
 * 
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

    static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    static {
        OPTIONS.setFollowReferrals(false);
    }

    // ------------------------------------------------------------------------

    @BeforeClass
    public static void beforeClass() {
        InstanceFactory.provide(InstanceGuice.withModule(new InternalModule()));
    }

    // ------------------------------------------------------------------------

    public static class InternalModule extends AbstractModule {

        @Provides
        @Singleton
        public FederatedEngine buildFederatedEngine() {
            try {
                return new FederatedEngineBuilder() //
                    .withDataSource(InstanceFactory.getInstance(DataSource.class)) //
                    // .withDatabaseSchemaUpdate(FederatedEngineBuilder.DB_SCHEMA_UPDATE_DROP_CREATE) //
                    .withDatabaseSchemaUpdate(FederatedEngineBuilder.DB_SCHEMA_UPDATE_FALSE)
                    .build();
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        @Provides
        @Singleton
        public JuelExpressionFactory buildJuelExpressionFactory() {
            return new JuelExpressionFactory(new ExpressionFactoryImpl());
        }

        @Provides
        public IdentitySession buildSession() throws Exception {
            return new IdentitySessionLdap(buildConnection());
        }

        @Provides
        public LdapConnection buildConnection() throws Exception {
            return new LdapConnection(LDAP_SEARCH_BASE, new LDAPConnection(OPTIONS, LDAP_PROVIDER_URL, 1389, LDAP_SECURITY_PRINCIPAL, LDAP_SECURITY_CREDENTIALS));
        }

        @Provides
        @Singleton
        private DataSource buildDataSource() {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.postgresql.Driver");
            config.setJdbcUrl("jdbc:postgresql://portal.cecdat.dev:5432/vlsp");
            config.setUsername("vlsp");
            config.setPassword("changeit");
            HikariDataSource source = new HikariDataSource(config);
            createIfNotExists(source);
            return InstanceClosingCheck.hook(source);
        }

        public void createIfNotExists(DataSource source) {
            try ( //
                Connection connection = source.getConnection(); //
                Statement statement = connection.createStatement(); //
            ) {
                {
                    URL url = Resources.getResource("sqls/create-if-not-exists-core.sql");
                    statement.execute(Resources.toString(url, StandardCharsets.UTF_8));
                }
            } catch (SQLException | IOException e) {
                throw new Error(e);
            }
        }

    }

}
