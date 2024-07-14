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

import java.time.Duration;
import java.util.UUID;

import javax.sql.DataSource;

import page.foliage.common.ioc.InstanceFactory;
import page.foliage.guava.common.cache.Cache;
import page.foliage.guava.common.cache.CacheBuilder;
import page.foliage.ldap.session.impl.CachedIdentitySession;
import page.foliage.ldap.session.impl.JdbcIdentitySession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class IdentitySessionFactory {

    // ------------------------------------------------------------------------

    private static volatile Cache<UUID, Object> cache;

    // ------------------------------------------------------------------------

    public static Cache<UUID, Object> cache() {
        Cache<UUID, Object> result = cache;
        if (result == null) {
            synchronized (IdentitySessionFactory.class) {
                if (result == null) {
                    CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
                    builder = builder.maximumSize(10000);
                    builder = builder.expireAfterWrite(Duration.ofHours(1));
                    result = cache = builder.build();
                }
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------

    public static IdentitySession openSession() throws Exception {
        return new CachedIdentitySession(InstanceFactory.getInstance(IdentitySession.class), cache());
    }

    public static IdentitySession openJdbcSession() throws Exception {
        IdentitySession ldelegate = InstanceFactory.getInstance(IdentitySession.class);
        IdentitySession jdelegate = new JdbcIdentitySession(ldelegate, InstanceFactory.getInstance(DataSource.class).getConnection());
        return new CachedIdentitySession(jdelegate, cache());
    }

}
