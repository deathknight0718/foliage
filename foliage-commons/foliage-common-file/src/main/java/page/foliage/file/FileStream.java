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

import okhttp3.Headers;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * @author deathknight0718@qq.com
 */
public class FileStream extends FilterInputStream {

    // ------------------------------------------------------------------------

    private final Headers headers;

    private final FilePoint point;

    // ------------------------------------------------------------------------

    public FileStream(Headers headers, FilePoint point, InputStream body) {
        super(body);
        this.headers = headers;
        this.point = point;
    }

    // ------------------------------------------------------------------------

    public Headers getHeaders() {
        return headers;
    }

    public FilePoint getPoint() {
        return point;
    }

}
