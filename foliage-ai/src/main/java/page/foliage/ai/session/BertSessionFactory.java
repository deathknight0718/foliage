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
package page.foliage.ai.session;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import page.foliage.ai.Result;
import page.foliage.ai.Tokenizer;
import page.foliage.ai.func.SpaceFunctions;
import page.foliage.common.util.ResourceUtils;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class BertSessionFactory implements AutoCloseable {

    // ------------------------------------------------------------------------

    private Path path;

    private Tokenizer tokenizer;

    private OrtEnvironment environment = OrtEnvironment.getEnvironment();

    private volatile PooledSession sessionPooled;

    // ------------------------------------------------------------------------

    private BertSessionFactory() {}

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        if (sessionPooled != null) sessionPooled.close();
    }

    // ------------------------------------------------------------------------

    public BertSession openPooledSession(int gpuId) throws Exception {
        PooledSession result = sessionPooled;
        if (result == null) {
            synchronized (this) {
                if ((result = sessionPooled) == null) sessionPooled = result = new PooledSession(openSession(gpuId));
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------

    public BertSession openSession(int gpuId) throws Exception {
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        options.addCUDA(0);
        OrtSession session = environment.createSession(path.toString(), options);
        return new InternalSession(options, session);
    }

    public BertSession openSession() throws Exception {
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        OrtSession session = environment.createSession(path.toString(), options);
        return new InternalSession(options, session);
    }

    // ------------------------------------------------------------------------

    public class PooledSession implements BertSession {

        private final BertSession delegate;

        public PooledSession(BertSession delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() throws Exception {}

        public Result run(File file) throws Exception {
            return delegate.run(file);
        }

        public Result run(String text) throws Exception {
            return delegate.run(text);
        }

    }

    // ------------------------------------------------------------------------

    public class InternalSession implements BertSession {

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

    public static class InternalResult implements Result {

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
            return SpaceFunctions.mean(lastHiddenState(), 1);
        }

    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private BertSessionFactory bean = new BertSessionFactory();

        public Builder withModel(Path path) {
            bean.path = path;
            return this;
        }

        public Builder withTokenizer(Tokenizer tokenizer) {
            bean.tokenizer = tokenizer;
            return this;
        }

        public BertSessionFactory build() {
            return bean;
        }

    }

}
