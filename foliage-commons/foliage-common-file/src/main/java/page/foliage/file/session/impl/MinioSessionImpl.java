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
package page.foliage.file.session.impl;

import java.io.InputStream;

import io.minio.DeleteObjectTagsArgs;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import page.foliage.file.MinioPoint;
import page.foliage.file.session.MinioSession;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class MinioSessionImpl implements MinioSession {

    // ------------------------------------------------------------------------

    private final long PART_SIZE_LOWER = 5L * 1024L * 1024L;

    private final MinioClient delegate;

    // ------------------------------------------------------------------------

    public MinioSessionImpl(MinioClient delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {}

    // ------------------------------------------------------------------------

    @Override
    public InputStream read(MinioPoint bean) throws Exception {
        GetObjectArgs.Builder query = GetObjectArgs.builder();
        query = query.bucket(bean.getBucket()).object(bean.getName());
        return delegate.getObject(query.build());
    }

    @Override
    public void write(MinioPoint bean, InputStream is) throws Exception {
        PutObjectArgs.Builder builder = PutObjectArgs.builder();
        try (is) {
            builder = builder.bucket(bean.getBucket()).object(bean.getName()).stream(is, -1, PART_SIZE_LOWER);
            delegate.putObject(builder.build());
        }
    }

    @Override
    public void remove(MinioPoint bean) throws Exception {
        DeleteObjectTagsArgs.Builder builder = DeleteObjectTagsArgs.builder();
        builder = builder.bucket(bean.getBucket()).object(bean.getName());
        delegate.deleteObjectTags(builder.build());
    }

}
