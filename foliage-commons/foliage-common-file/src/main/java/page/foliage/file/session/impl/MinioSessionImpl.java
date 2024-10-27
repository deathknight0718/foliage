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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.minio.*;
import io.minio.messages.Tags;
import org.apache.commons.lang3.StringUtils;
import page.foliage.file.FileMetadata;
import page.foliage.file.FileObjectStream;
import page.foliage.file.FilePoint;
import page.foliage.file.session.FileSession;

import java.io.InputStream;
import java.util.Map;

/**
 * @author deathknight0718@qq.com
 */
public class MinioSessionImpl implements FileSession {

    // ------------------------------------------------------------------------

    private final static long PART_SIZE_LOWER = 5L * 1024L * 1024L;

    private final MinioClient delegate;

    // ------------------------------------------------------------------------

    public MinioSessionImpl(MinioClient delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() {}

    // ------------------------------------------------------------------------

    @Override
    public FileMetadata metadata(FilePoint point) throws Exception {
        GetObjectTagsArgs.Builder query = GetObjectTagsArgs.builder();
        if (StringUtils.isNotEmpty(point.getRegion())) query = query.region(point.getRegion());
        query = query.bucket(point.getBucket()).object(point.getName());
        Tags tags = delegate.getObjectTags(query.build());
        return new FileMetadata(tags.get());
    }

    @Override
    public FileObjectStream stream(FilePoint point) throws Exception {
        GetObjectArgs.Builder query = GetObjectArgs.builder();
        if (StringUtils.isNotEmpty(point.getRegion())) query = query.region(point.getRegion());
        query = query.bucket(point.getBucket()).object(point.getName());
        GetObjectResponse response = delegate.getObject(query.build());
        return new FileObjectStream(response.headers(), point, response);
    }

    @Override
    public void upload(FilePoint point, InputStream is) throws Exception {
        this.upload(point, is, ImmutableMultimap.of());
    }

    @Override
    public void upload(FilePoint point, InputStream is, Map<String, String> headers) throws Exception {
        this.upload(point, is, ImmutableMultimap.copyOf(headers.entrySet()));
    }

    @Override
    public void upload(FilePoint point, InputStream is, Multimap<String, String> headers) throws Exception {
        PutObjectArgs.Builder builder = PutObjectArgs.builder();
        try (is) {
            if (StringUtils.isNotEmpty(point.getRegion())) builder = builder.region(point.getRegion());
            builder = builder.bucket(point.getBucket());
            builder = builder.object(point.getName());
            builder = builder.stream(is, -1, PART_SIZE_LOWER);
            builder = builder.headers(headers);
            delegate.putObject(builder.build());
        }
    }

    @Override
    public void remove(FilePoint point) throws Exception {
        DeleteObjectTagsArgs.Builder builder = DeleteObjectTagsArgs.builder();
        if (StringUtils.isNotEmpty(point.getRegion())) builder = builder.region(point.getRegion());
        builder = builder.bucket(point.getBucket()).object(point.getName());
        delegate.deleteObjectTags(builder.build());
    }

}
