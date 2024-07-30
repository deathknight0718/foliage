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
package page.foliage.ai.candle;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import page.foliage.ai.ModelSession;
import page.foliage.ai.Result;
import page.foliage.common.util.ResourceUtils;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class CandleSessionFactory implements AutoCloseable {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(CandleSessionFactory.class);

    private static final int DEFAULT_MAX_SIZE = 4;

    private int size = DEFAULT_MAX_SIZE;

    private CandleModel.Builder builder = CandleModel.builder();

    private final AtomicInteger count = new AtomicInteger(0);

    private volatile ArrayBlockingQueue<ModelSession> pool;

    // ------------------------------------------------------------------------

    private CandleSessionFactory() {}

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        synchronized (this) {
            List<ModelSession> list = new ArrayList<>(size);
            pool.drainTo(list);
            LOGGER.debug("close all object: {}", list.size());
            list.stream().forEach(ResourceUtils::safeClose);
        }
    }

    // ------------------------------------------------------------------------

    public ModelSession openPooledSession(int gpuId) throws Exception {
        LOGGER.debug("count of session is {}, pool size is {}", count.get(), pool.size());
        if (count.get() < size) {
            synchronized (this) {
                if (count.get() < size) {
                    ModelSession instance = openSession();
                    count.incrementAndGet();
                    return new PooledSession(instance);
                }
            }
        }
        return new PooledSession(pool.poll(30, TimeUnit.SECONDS));
    }

    // ------------------------------------------------------------------------

    public ModelSession openSession() throws Exception {
        return new CandleSession(builder.build());
    }

    // ------------------------------------------------------------------------

    public class PooledSession implements ModelSession {

        private final ModelSession delegate;

        public PooledSession(ModelSession delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() throws Exception {
            pool.put(delegate);
        }

        public Result run(File file) throws Exception {
            return delegate.run(file);
        }

        public Result run(String text) throws Exception {
            return delegate.run(text);
        }

    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private CandleSessionFactory bean = new CandleSessionFactory();

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

        public Builder withPoolSize(int size) {
            bean.size = size;
            return this;
        }

        public CandleSessionFactory build() {
            bean.pool = new ArrayBlockingQueue<ModelSession>(bean.size);
            return bean;
        }

    }

}
