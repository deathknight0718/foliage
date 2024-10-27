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

import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import page.foliage.common.collect.Identities;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.file.session.FileSession;
import page.foliage.file.session.FileSessionFactory;

/**
 * @author deathknight0718@qq.com
 */
public class FilePoint {

    // ------------------------------------------------------------------------

    private UUID id;

    private String region, name, bucket;

    // ------------------------------------------------------------------------

    private FilePoint() {}

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    private static FileSessionFactory factory() {
        return InstanceFactory.getInstance(FileSessionFactory.class);
    }

    // ------------------------------------------------------------------------

    public FileMetadata metadata() {
        try (FileSession session = factory().openSession()) {
            return session.metadata(this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public FileObjectStream stream() {
        try (FileSession session = factory().openSession()) {
            return session.stream(this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void upload(InputStream is) {
        try (is; FileSession session = factory().openSession()) {
            session.upload(this, is);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void remove() {
        try (FileSession session = factory().openSession()) {
            session.remove(this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------------------------

    public UUID getId() {
        return id;
    }

    public String getRegion() {
        return region;
    }

    public String getBucket() {
        return bucket;
    }

    public String getName() {
        return name;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private final FilePoint bean = new FilePoint();

        public Builder withRegion(String region) {
            bean.region = region;
            return this;
        }

        public Builder withBucket(String bucket) {
            bean.bucket = bucket;
            return this;
        }

        public Builder withName(String name) {
            bean.name = name;
            return this;
        }

        public FilePoint build() {
            if (StringUtils.isEmpty(bean.region)) bean.id = Identities.uuid(bean.bucket, bean.name);
            else bean.id = Identities.uuid(bean.region, bean.bucket, bean.name);
            return bean;
        }

    }

}
