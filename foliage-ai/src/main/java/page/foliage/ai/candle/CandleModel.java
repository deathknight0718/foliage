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
import java.nio.file.Files;
import java.nio.file.Path;

import page.foliage.guava.common.base.Preconditions;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class CandleModel implements AutoCloseable {

    // ------------------------------------------------------------------------

    private final static CandleLibrary LIBRARY = CandleLibrary.instance();

    private long id;

    private int gpuId = -1;

    private Path path;

    private CandleTokenizer tokenizer;

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        LIBRARY.modelDelete(id);
        tokenizer.close();
    }

    // ------------------------------------------------------------------------

    public CandleResult embeddings(String text) throws Exception {
        try (CandleEncoding encoding = tokenizer.encode(text)) {
            return embeddings(encoding);
        }
    }

    public CandleResult embeddings(String[] texts) throws Exception {
        try (CandleEncodingVector vector = tokenizer.encodes(texts)) {
            return embeddings(vector);
        }
    }

    public CandleResult embeddings(CandleEncoding encoding) {
        return new CandleResult(LIBRARY.embeddingsCreate(id, encoding.getId()));
    }

    public CandleResult embeddings(CandleEncodingVector vector) {
        return new CandleResult(LIBRARY.embeddingsCreateInBatch(id, vector.getId()));
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private CandleModel bean = new CandleModel();

        private CandleTokenizer.Builder builder = CandleTokenizer.builder();

        public Builder withFile(File file) {
            return withPath(file.toPath());
        }

        public Builder withPath(Path path) {
            Preconditions.checkArgument(Files.isDirectory(path));
            bean.path = path.toAbsolutePath();
            builder.withPath(bean.path);
            return this;
        }

        public Builder withPadding(boolean padding) {
            builder.withPadding(padding);
            return this;
        }

        public Builder withTruncation(boolean truncation) {
            builder.withTruncation(truncation);
            return this;
        }

        public Builder withGpuId(int gpuId) {
            bean.gpuId = gpuId;
            return this;
        }

        public CandleModel build() {
            try {
                bean.tokenizer = builder.build();
                bean.id = LIBRARY.modelCreate(bean.gpuId, bean.path.toAbsolutePath().toString());
                return bean;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

}
