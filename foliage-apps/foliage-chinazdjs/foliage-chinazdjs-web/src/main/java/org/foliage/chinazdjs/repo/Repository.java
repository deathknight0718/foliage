/*
 * Copyright 2022 Deathknight0718.
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
package org.foliage.chinazdjs.repo;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Repository {

    private static volatile BasicDataSource source;

    private static BasicDataSource source() {
        if (source == null) {
            synchronized (Repository.class) {
                if (source == null) {
                    source = new BasicDataSource();
                    source.setDriverClassName("org.postgresql.Driver");
                    source.setUrl("jdbc:postgresql://dm-002:15432/demo");
                    source.setUsername("postgres");
                    source.setPassword("9wMo8o}Vxf)q");
                }
            }
        }
        return source;
    }

    public void insertDevice() throws SQLException {
        try (Connection connection = source().getConnection()) {

        }
    }

    public void insertGeographic() {

    }

}
