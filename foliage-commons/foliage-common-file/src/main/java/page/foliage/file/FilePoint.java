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

import page.foliage.common.collect.Identities;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.file.session.FileSession;
import page.foliage.file.session.FileSessionFactory;
import page.foliage.guava.common.base.Preconditions;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author deathknight0718@qq.com
 */
public class FilePoint {

    // ------------------------------------------------------------------------

    private FileRegion region;

    private FileBucket bucket;

    private UUID id;

    private String name;

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

    public List<FilePoint> list(boolean recursive) {
        try (FileSession session = factory().openSession()) {
            return session.points(this, recursive);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public FileTags tags() {
        try (FileSession session = factory().openSession()) {
            return session.tags(this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public FileStream stream() {
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

    public void upload(InputStream is, Map<String, String> tags) {
        try (is; FileSession session = factory().openSession()) {
            session.upload(this, is, tags);
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

    public FileRegion getRegion() {
        return region;
    }

    public FileBucket getBucket() {
        return bucket;
    }

    public String getName() {
        return name;
    }

    // ------------------------------------------------------------------------

    interface StepRegion {

        StepBucket withRegion(FileRegion region);

        StepBucket withRegion(Long id);

    }

    // ------------------------------------------------------------------------

    interface StepBucket {

        StepName withBucket(FileBucket bucket);

        StepName withBucket(String bucket);

    }

    // ------------------------------------------------------------------------

    interface StepName {

        Builder withName(String name);

    }

    // ------------------------------------------------------------------------

    interface StepBuilder {

        FilePoint build();

    }

    // ------------------------------------------------------------------------

    public static class Builder implements StepRegion, StepBucket, StepName, StepBuilder {

        private final FilePoint bean = new FilePoint();

        public Builder withRegion(FileRegion region) {
            bean.region = region;
            return this;
        }

        public Builder withRegion(Long id) {
            bean.region = FileRegion.get(id);
            return this;
        }

        public Builder withBucket(FileBucket bucket) {
            bean.region = bucket.region();
            bean.bucket = bucket;
            return this;
        }

        public Builder withBucket(String name) {
            Preconditions.checkNotNull(bean.region);
            bean.bucket = new FileBucket(bean.region, name);
            return this;
        }

        public Builder withName(String name) {
            Preconditions.checkNotNull(bean.bucket);
            bean.name = name;
            return this;
        }

        public FilePoint build() {
            Preconditions.checkNotNull(bean.name);
            bean.id = Identities.uuid(bean.region.getName(), bean.bucket.name(), bean.name);
            return bean;
        }

    }

}
