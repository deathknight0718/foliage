/*
 * Copyright 2024 Foliage Develop Team.
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
package page.foliage.file;

import page.foliage.ldap.Repository;
import page.foliage.ldap.session.IdentitySession;

import static page.foliage.ldap.session.IdentitySessionFactory.openSession;

/**
 * @author deathknight0718@qq.com
 */
public class FileRegion {

    // ------------------------------------------------------------------------

    public static final String PREFIX = "cn-";

    // ------------------------------------------------------------------------

    private final Repository repository;

    private final String name;

    // ------------------------------------------------------------------------

    private FileRegion(Repository repository) {
        this(repository, PREFIX + repository.getId());
    }

    private FileRegion(Repository repository, String name) {
        this.repository = repository;
        this.name = name;
    }

    // ------------------------------------------------------------------------

    public static FileRegion create(Long id) {
        return new FileRegion(Repository.create(id));
    }

    public static FileRegion get(Long id) {
        return new FileRegion(Repository.get(id));
    }

    // ------------------------------------------------------------------------

    public Long getId() {
        return repository.getId();
    }

    public String getName() {
        return name;
    }

    public Repository getRepository() {
        return repository;
    }

}
