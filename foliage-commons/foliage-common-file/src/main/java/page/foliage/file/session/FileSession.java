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
package page.foliage.file.session;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.file.*;

import java.io.InputStream;
import java.util.Map;

/**
 * @author deathknight0718@qq.com
 */
public interface FileSession extends AutoCloseable {

    // ------------------------------------------------------------------------

    long PART_SIZE_LOWER = 5L * 1024L * 1024L;

    String HEADER_REGION = "X-Amz-Bucket-Region";

    String TAG_ID = "id";

    String TAG_INTERPRETER = "interpreter";

    // ------------------------------------------------------------------------

    PaginList<FileBucket> bucketsByParams(QueryParams params, FileRegion region) throws Exception;

    PaginList<FilePoint> pointsByParamsAndBucket(QueryParams params, FileBucket bucket) throws Exception;

    PaginList<FilePoint> pointsByParamsAndPrefix(QueryParams params, FilePoint point) throws Exception;

    FileTags tags(FilePoint point) throws Exception;

    FileStream stream(FilePoint point) throws Exception;

    void upload(FilePoint point, InputStream is) throws Exception;

    void upload(FilePoint point, InputStream is, Map<String, String> tags) throws Exception;

    void remove(FilePoint point) throws Exception;

}
