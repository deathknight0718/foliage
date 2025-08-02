/*
 * Copyright 2025 Foliage Develop Team.
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
package page.foliage.flow;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import page.foliage.common.ioc.InstanceClosingCheck;
import page.foliage.guava.common.io.Resources;
import page.foliage.inject.AbstractModule;
import page.foliage.inject.Provides;
import page.foliage.inject.Singleton;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class TestModuleJdbc extends AbstractModule {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(TestModuleJdbc.class);

    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    private static final String JDBC_URL = "jdbc:postgresql://portal.cecdat.dev:5432/vlsp";

    private static final String JDBC_USERNAME = "vlsp";

    private static final String JDBC_PASSWORD = "changeit";

    private static final String SQL_CREATE_IF_NOT_EXISTS_CORE = "sqls/create-if-not-exists-core.sql";

    private static final String SQL_CREATE_IF_NOT_EXISTS_FLOW = "sqls/create-if-not-exists-flow.sql";

    private static final String SQL_RESET_FLOW = "sqls/reset-flow.sql";

    // ------------------------------------------------------------------------

    @Provides
    @Singleton
    private DataSource buildDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(JDBC_DRIVER);
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(JDBC_USERNAME);
        config.setPassword(JDBC_PASSWORD);
        HikariDataSource source = createIfNotExists(new HikariDataSource(config));
        return InstanceClosingCheck.hook(source);
    }

    public HikariDataSource createIfNotExists(HikariDataSource source) {
        try ( //
            Connection connection = source.getConnection(); //
            Statement statement = connection.createStatement(); //
        ) {
            {
                URL url = Resources.getResource(SQL_RESET_FLOW);
                statement.execute(Resources.toString(url, StandardCharsets.UTF_8));
            }
            {
                URL url = Resources.getResource(SQL_CREATE_IF_NOT_EXISTS_CORE);
                statement.execute(Resources.toString(url, StandardCharsets.UTF_8));
            }
            {
                URL url = Resources.getResource(SQL_CREATE_IF_NOT_EXISTS_FLOW);
                statement.execute(Resources.toString(url, StandardCharsets.UTF_8));
            }
            return source;
        } catch (Exception e) {
            LOGGER.error("Failed to create database schema", e);
            throw new Error(e);
        }
    }

}
