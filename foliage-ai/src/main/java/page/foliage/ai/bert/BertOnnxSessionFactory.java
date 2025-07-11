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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import page.foliage.ai.Functions;
import page.foliage.common.util.ResourceUtils;
import page.foliage.guava.common.base.Preconditions;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class BertOnnxSessionFactory implements AutoCloseable {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(BertOnnxSessionFactory.class);

    private static final OrtEnvironment environment = OrtEnvironment.getEnvironment();

    private static final int DEFAULT_MAX_SIZE = 4;

    private int size = DEFAULT_MAX_SIZE;

    private Path path;

    private BertOnnxTokenizer tokenizer;

    private final AtomicInteger count = new AtomicInteger(0);

    private volatile ArrayBlockingQueue<BertModelSession> pool;

    // ------------------------------------------------------------------------

    private BertOnnxSessionFactory() {}

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
            ResourceUtils.safeClose(tokenizer);
        }
    }

    // ------------------------------------------------------------------------

    public BertModelSession openPooledSession(int gpuId) throws Exception {
        LOGGER.debug("count of session is {}, pool size is {}", count.get(), pool.size());
        if (count.get() < size) {
            synchronized (this) {
                if (count.get() < size) {
                    BertModelSession instance = openSession(gpuId);
                    count.incrementAndGet();
                    return new PooledSession(instance);
                }
            }
        }
        return new PooledSession(pool.poll(30, TimeUnit.SECONDS));
    }

    // ------------------------------------------------------------------------

    public BertModelSession openSession(int gpuId) throws Exception {
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        options.addCUDA(0);
        OrtSession session = environment.createSession(path.toString(), options);
        return new InternalSession(options, session);
    }

    public BertModelSession openSession() throws Exception {
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        OrtSession session = environment.createSession(path.toString(), options);
        return new InternalSession(options, session);
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

    public class InternalSession implements BertModelSession {

        private final OrtSession.SessionOptions options;

        private final OrtSession delegate;

        public InternalSession(OrtSession.SessionOptions options, OrtSession delegate) {
            this.options = options;
            this.delegate = delegate;
        }

        @Override
        public void close() throws Exception {
            ResourceUtils.safeClose(delegate, options);
        }

        public InternalResult run(File file) throws OrtException, IOException {
            return run(Files.readString(file.toPath(), StandardCharsets.UTF_8));
        }

        public InternalResult run(String text) throws OrtException {
            return new InternalResult(delegate.run(tokenizer.encode(text)));
        }

    }

    // ------------------------------------------------------------------------

    public static class InternalResult implements BertResult {

        private final ai.onnxruntime.OrtSession.Result delegate;

        public InternalResult(ai.onnxruntime.OrtSession.Result delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() throws Exception {
            delegate.close();
        }

        @Override
        public float[][][] lastHiddenState() throws Exception {
            return (float[][][]) delegate.get("last_hidden_state").get().getValue();
        }

        @Override
        public float[][] embeddings() throws Exception {
            return Functions.mean(lastHiddenState(), 1);
        }

    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private BertOnnxSessionFactory bean = new BertOnnxSessionFactory();
        
        private BertOnnxTokenizer.Builder builder;

        public Builder withDirectory(Path path) {
            return withDirectory(path.toFile());
        }

        public Builder withDirectory(File file) {
            Preconditions.checkArgument(file.isDirectory());
            withTokenizer(file);
            for (File children : file.listFiles()) {
                if (children.getName().toLowerCase().endsWith(".onnx")) withModel(children);
            }
            return this;
        }

        public Builder withModel(File file) {
            return withModel(file.toPath());
        }

        public Builder withModel(Path path) {
            bean.path = path.toAbsolutePath();
            return this;
        }

        public Builder withTokenizer(File file) {
            return withTokenizer(file.toPath());
        }

        public Builder withTokenizer(Path path) {
            return withTokenizer(BertOnnxTokenizer.builder().withPath(path));
        }

        public Builder withTokenizer(BertOnnxTokenizer.Builder builder) {
            this.builder = builder;
            return this;
        }

        public Builder withPoolSize(int size) {
            bean.size = size;
            return this;
        }

        public BertOnnxSessionFactory build() {
            Preconditions.checkNotNull(bean.path);
            Preconditions.checkNotNull(builder);
            bean.tokenizer = builder.build();
            bean.pool = new ArrayBlockingQueue<BertModelSession>(bean.size);
            return bean;
        }

    }

}
