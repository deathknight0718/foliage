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
package page.foliage.flow;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.text.StringSubstitutor;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.guava.common.io.Resources;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FederatedEngineBuilder {

    // ------------------------------------------------------------------------

    public static final String DB_SCHEMA_UPDATE_DROP_CREATE = AbstractEngineConfiguration.DB_SCHEMA_UPDATE_DROP_CREATE;

    public static final String DB_SCHEMA_UPDATE_FALSE = AbstractEngineConfiguration.DB_SCHEMA_UPDATE_FALSE;

    public static final String DB_SCHEMA = "flow";

    // ------------------------------------------------------------------------

    private DataSource source;

    private String databaseSchemaUpdate;

    // ------------------------------------------------------------------------

    private static void createSchemaIfNotExists(DataSource source) {
        try ( //
            Connection connection = source.getConnection(); //
            Statement statement = connection.createStatement(); //
        ) {
            statement.execute(String.format("CREATE SCHEMA IF NOT EXISTS %s;", DB_SCHEMA));
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    private static void createExternalIfNotExists(DataSource source) {
        try ( //
            Connection connection = source.getConnection(); //
            Statement statement = connection.createStatement(); //
        ) {
            URL url = Resources.getResource("sqls/create-if-not-exists-form.sql");
            String template = Resources.toString(url, StandardCharsets.UTF_8);
            StringSubstitutor substitutor = new StringSubstitutor(ImmutableMap.of("schema", DB_SCHEMA));
            statement.execute(substitutor.replace(template));
        } catch (SQLException | IOException e) {
            throw new Error(e);
        }
    }

    // ------------------------------------------------------------------------

    public FederatedEngineBuilder withDataSource(DataSource source) {
        this.source = source;
        return this;
    }

    public FederatedEngineBuilder withDatabaseSchemaUpdate(String databaseSchemaUpdate) {
        this.databaseSchemaUpdate = databaseSchemaUpdate;
        return this;
    }

    // ------------------------------------------------------------------------

    public FederatedEngine build() throws SQLException, IOException {
        ProcessEngineConfiguration pec = new StandaloneProcessEngineConfiguration();
        pec.setDataSource(source);
        pec.setDatabaseType(ProcessEngineConfiguration.DATABASE_TYPE_POSTGRES);
        createSchemaIfNotExists(source);
        pec.setDatabaseSchema(DB_SCHEMA);
        pec.setDatabaseSchemaUpdate(databaseSchemaUpdate);
        pec.setIdmEngineConfigurator(new FederatedIdentityConfigurator());
        ProcessEngine engine = pec.buildProcessEngine();
        if (DB_SCHEMA_UPDATE_DROP_CREATE.equals(databaseSchemaUpdate)) createExternalIfNotExists(source);
        return new FederatedEngine(source, DB_SCHEMA, engine);
    }

}
