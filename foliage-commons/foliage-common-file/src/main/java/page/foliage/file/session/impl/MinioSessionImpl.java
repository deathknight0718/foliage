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

import io.minio.*;
import io.minio.messages.Item;
import io.minio.messages.Tags;
import page.foliage.file.FileBucket;
import page.foliage.file.FilePoint;
import page.foliage.file.FileStream;
import page.foliage.file.FileTags;
import page.foliage.file.session.FileSession;
import page.foliage.guava.common.collect.ImmutableList;
import page.foliage.guava.common.collect.ImmutableMap;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author deathknight0718@qq.com
 */
public class MinioSessionImpl implements FileSession {

    // ------------------------------------------------------------------------

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
    public List<FilePoint> points(FileBucket bucket) throws Exception {
        ListObjectsArgs.Builder query = ListObjectsArgs.builder();
        query.region(bucket.region().getName());
        query.extraHeaders(ImmutableMap.of(HEADER_REGION, bucket.region().getName()));
        query.bucket(bucket.name());
        query.recursive(false);
        ImmutableList.Builder<FilePoint> listBuilder = ImmutableList.builder();
        for (Result<Item> result : delegate.listObjects(query.build())) {
            FilePoint.Builder itemBuilder = FilePoint.builder();
            FilePoint item = itemBuilder.withRegion(bucket.region()).withBucket(bucket.name()).withName(result.get().objectName()).build();
            listBuilder.add(item);
        }
        return listBuilder.build();
    }

    @Override
    public List<FilePoint> points(FilePoint point, boolean recursive) throws Exception {
        ListObjectsArgs.Builder query = ListObjectsArgs.builder();
        query.region(point.getRegion().getName());
        query.extraHeaders(ImmutableMap.of(HEADER_REGION, point.getRegion().getName()));
        query.bucket(point.getBucket().name());
        query.prefix(point.getName());
        query.recursive(recursive);
        ImmutableList.Builder<FilePoint> listBuilder = ImmutableList.builder();
        for (Result<Item> result : delegate.listObjects(query.build())) {
            FilePoint.Builder itemBuilder = FilePoint.builder();
            FilePoint item = itemBuilder.withRegion(point.getRegion()).withBucket(point.getBucket().name()).withName(result.get().objectName()).build();
            listBuilder.add(item);
        }
        return listBuilder.build();
    }

    @Override
    public FileTags tags(FilePoint point) throws Exception {
        GetObjectTagsArgs.Builder query = GetObjectTagsArgs.builder();
        query.region(point.getRegion().getName());
        query.extraHeaders(ImmutableMap.of(HEADER_REGION, point.getRegion().getName()));
        query.bucket(point.getBucket().name());
        query.object(point.getName());
        Tags tags = delegate.getObjectTags(query.build());
        return new FileTags(tags.get());
    }

    @Override
    public FileStream stream(FilePoint point) throws Exception {
        GetObjectArgs.Builder query = GetObjectArgs.builder();
        query.region(point.getRegion().getName());
        query.extraHeaders(ImmutableMap.of(HEADER_REGION, point.getRegion().getName()));
        query.bucket(point.getBucket().name());
        query.object(point.getName());
        GetObjectResponse response = delegate.getObject(query.build());
        return new FileStream(response.headers(), point, response);
    }

    @Override
    public void upload(FilePoint point, InputStream is) throws Exception {
        this.upload(point, is, ImmutableMap.of());
    }

    @Override
    public void upload(FilePoint point, InputStream is, Map<String, String> tags) throws Exception {
        PutObjectArgs.Builder builder = PutObjectArgs.builder();
        try (is) {
            builder.region(point.getRegion().getName());
            builder.extraHeaders(ImmutableMap.of(HEADER_REGION, point.getRegion().getName()));
            builder.bucket(point.getBucket().name());
            builder.object(point.getName());
            builder.stream(is, -1, PART_SIZE_LOWER);
            Map<String, String> merged = new HashMap<>(tags);
            merged.put(TAG_ID, point.getId().toString());
            builder.tags(merged);
            delegate.putObject(builder.build());
        }
    }

    @Override
    public void remove(FilePoint point) throws Exception {
        DeleteObjectTagsArgs.Builder builder = DeleteObjectTagsArgs.builder();
        builder.region(point.getRegion().getName());
        builder.extraHeaders(ImmutableMap.of(HEADER_REGION, point.getRegion().getName()));
        builder.bucket(point.getBucket().name());
        builder.object(point.getName());
        delegate.deleteObjectTags(builder.build());
    }

}
