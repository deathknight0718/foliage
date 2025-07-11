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

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.file.session.FileSession;
import page.foliage.file.session.FileSessionFactory;

import java.time.ZonedDateTime;

/**
 * @author deathknight0718@qq.com
 */
public record FileBucket(FileRegion region, String name, ZonedDateTime timestamp) {

    // ------------------------------------------------------------------------

    public FileBucket(FileRegion region, String name) {
        this(region, name, ZonedDateTime.now());
    }

    // ------------------------------------------------------------------------

    private static FileSessionFactory factory() {
        return InstanceFactory.getInstance(FileSessionFactory.class);
    }

    // ------------------------------------------------------------------------

    public PaginList<FilePoint> points(QueryParams params) {
        try (FileSession session = factory().openSession()) {
            return session.pointsByParamsAndBucket(params, this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public ZonedDateTime timestamp() {
        return timestamp;
    }

}
