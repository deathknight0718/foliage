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

import page.foliage.common.collect.Identities;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.file.session.MinioSession;
import page.foliage.file.session.MinioSessionFactory;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class MinioPoint {

    // ------------------------------------------------------------------------

    private UUID id;

    private String name, bucket;

    // ------------------------------------------------------------------------

    private MinioPoint() {}

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    private static MinioSessionFactory factory() {
        return InstanceFactory.getInstance(MinioSessionFactory.class);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("resource")
    public InputStream read() {
        try (MinioSession session = factory().openSession()) {
            return session.read(this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void write(InputStream is) {
        try (is; MinioSession session = factory().openSession()) {
            session.write(this, is);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void remove() {
        try (MinioSession session = factory().openSession()) {
            session.remove(this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------------------------

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBucket() {
        return bucket;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private MinioPoint bean = new MinioPoint();

        public Builder withName(String name) {
            bean.name = name;
            return this;
        }

        public Builder withBucket(String bucket) {
            bean.bucket = bucket;
            return this;
        }

        public MinioPoint build() {
            bean.id = Identities.uuid(bean.bucket, bean.name);
            return bean;
        }

    }

}
