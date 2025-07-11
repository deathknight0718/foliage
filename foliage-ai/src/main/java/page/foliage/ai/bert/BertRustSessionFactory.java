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
package page.foliage.ai.bert;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import page.foliage.common.util.ResourceUtils;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class BertRustSessionFactory implements AutoCloseable {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(BertRustSessionFactory.class);

    private static final int DEFAULT_MAX_SIZE = 4;

    private int size = DEFAULT_MAX_SIZE;

    private BertRustModel.Builder builder = BertRustModel.builder();

    private final AtomicInteger count = new AtomicInteger(0);

    private volatile ArrayBlockingQueue<BertModelSession> pool;

    // ------------------------------------------------------------------------

    private BertRustSessionFactory() {}

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        synchronized (this) {
            List<BertModelSession> list = new ArrayList<>(size);
            pool.drainTo(list);
            LOGGER.debug("close all object: {}", list.size());
            list.stream().forEach(ResourceUtils::safeClose);
        }
    }

    // ------------------------------------------------------------------------

    public BertModelSession openPooledSession(int gpuId) throws Exception {
        LOGGER.debug("count of session is {}, pool size is {}", count.get(), pool.size());
        if (count.get() < size) {
            synchronized (this) {
                if (count.get() < size) {
                    BertModelSession instance = openSession();
                    count.incrementAndGet();
                    return new PooledSession(instance);
                }
            }
        }
        return new PooledSession(pool.poll(30, TimeUnit.SECONDS));
    }

    // ------------------------------------------------------------------------

    public BertModelSession openSession() throws Exception {
        return new BertRustSession(builder.build());
    }

    // ------------------------------------------------------------------------

    public class PooledSession implements BertModelSession {

        private final BertModelSession delegate;

        public PooledSession(BertModelSession delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() throws Exception {
            pool.put(delegate);
        }

        public BertResult run(File file) throws Exception {
            return delegate.run(file);
        }

        public BertResult run(String text) throws Exception {
            return delegate.run(text);
        }

    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private BertRustSessionFactory bean = new BertRustSessionFactory();

        public Builder withPath(Path path) {
            bean.builder.withPath(path);
            return this;
        }

        public Builder withPadding(boolean padding) {
            bean.builder.withPadding(padding);
            return this;
        }

        public Builder withTruncation(boolean truncation) {
            bean.builder.withTruncation(truncation);
            return this;
        }

        public Builder withGpuId(int gpuId) {
            bean.builder.withGpuId(gpuId);
            return this;
        }

        public Builder withMode(String mode) {
            bean.builder.withMode(mode);
            return this;
        }

        public Builder withPoolSize(int size) {
            bean.size = size;
            return this;
        }

        public BertRustSessionFactory build() {
            bean.pool = new ArrayBlockingQueue<BertModelSession>(bean.size);
            return bean;
        }

    }

}
