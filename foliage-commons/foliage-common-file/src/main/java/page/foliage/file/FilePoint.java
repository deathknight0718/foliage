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

import org.apache.commons.io.FilenameUtils;
import page.foliage.common.collect.Identities;
import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.file.session.FileSession;
import page.foliage.file.session.FileSessionFactory;
import page.foliage.guava.common.base.Preconditions;

import java.io.InputStream;
import java.time.ZonedDateTime;
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

    private String path;

    private ZonedDateTime timestamp;

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

    public PaginList<FilePoint> list(QueryParams params) {
        try (FileSession session = factory().openSession()) {
            return session.pointsByParamsAndPrefix(params, this);
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

    public boolean isDirectory() {
        return path.endsWith("/");
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

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return FilenameUtils.getName(path);
    }

    public String getParent() {
        return FilenameUtils.getPath(path);
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

        Builder withPath(String path);

        Builder withTimestamp(ZonedDateTime timestamp);

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

        public Builder withPath(String path) {
            Preconditions.checkNotNull(bean.bucket);
            bean.path = FilenameUtils.normalize(path);
            return this;
        }

        @Override
        public Builder withTimestamp(ZonedDateTime timestamp) {
            Preconditions.checkNotNull(bean.bucket);
            bean.timestamp = timestamp;
            return this;
        }

        public FilePoint build() {
            Preconditions.checkNotNull(bean.path);
            bean.id = Identities.uuid(bean.region.getName(), bean.bucket.name(), bean.path);
            return bean;
        }

    }

}
