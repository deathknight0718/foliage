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

import com.google.common.collect.Multimap;
import page.foliage.file.FileMetadata;
import page.foliage.file.FileObjectStream;
import page.foliage.file.FilePoint;

import java.io.InputStream;
import java.util.Map;

/**
 * @author deathknight0718@qq.com
 */
public interface FileSession extends AutoCloseable {

    FileMetadata metadata(FilePoint point) throws Exception;

    FileObjectStream stream(FilePoint point) throws Exception;

    void upload(FilePoint point, InputStream is) throws Exception;

    void upload(FilePoint point, InputStream is, Map<String, String> headers) throws Exception;

    void upload(FilePoint point, InputStream is, Multimap<String, String> headers) throws Exception;

    void remove(FilePoint point) throws Exception;

}
