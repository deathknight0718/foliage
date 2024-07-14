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

import java.util.concurrent.TimeUnit;

import io.minio.MinioClient;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.file.session.MinioSession;
import page.foliage.file.session.MinioSessionFactory;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class MinioSessionFactoryImpl implements MinioSessionFactory {

    // ------------------------------------------------------------------------

    private MinioClient client;

    // ------------------------------------------------------------------------

    private MinioSessionFactoryImpl(MinioClient client) {
        this.client = client;
    }

    // ------------------------------------------------------------------------

    public static MinioSessionFactoryImpl singleton() {
        return InstanceFactory.getInstance(MinioSessionFactoryImpl.class);
    }

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        client.close();
    }

    @Override
    public MinioSession openSession() throws Exception {
        return new MinioSessionImpl(client);
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private MinioClient.Builder builder = MinioClient.builder();

        private OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();

        public Builder endpoint(String endpoint) {
            builder.endpoint(endpoint);
            return this;
        }

        public Builder credentials(String accessKey, String secretKey) {
            builder.credentials(accessKey, secretKey);
            return this;
        }

        public Builder pool(int maxIdleConnections, long keepAliveDuration, TimeUnit unit) {
            httpBuilder.connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDuration, unit));
            return this;
        }

        public MinioSessionFactoryImpl build() {
            builder.httpClient(httpBuilder.build());
            return new MinioSessionFactoryImpl(builder.build());
        }

    }

}
