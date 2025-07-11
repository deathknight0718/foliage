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
import java.nio.file.Files;
import java.nio.file.Path;

import page.foliage.guava.common.base.Preconditions;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class BertRustModel implements AutoCloseable {

    // ------------------------------------------------------------------------
    
    public final static String MODE_PT = "PT";
    
    public final static String MODE_ST = "ST";

    // ------------------------------------------------------------------------

    private final static BertRustLibrary LIBRARY = BertRustLibrary.instance();

    private long id;

    private int gpuId = -1;

    private Path path;

    private String mode = MODE_PT;

    private BertRustTokenizer tokenizer;

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

    public BertRustResult embeddings(String text) throws Exception {
        try (BertRustEncoding encoding = tokenizer.encode(text)) {
            return embeddings(encoding);
        }
    }

    public BertRustResult embeddings(String[] texts) throws Exception {
        try (BertRustEncodingVector vector = tokenizer.encodes(texts)) {
            return embeddings(vector);
        }
    }

    public BertRustResult embeddings(BertRustEncoding encoding) {
        return new BertRustResult(LIBRARY.embeddingsCreate(id, encoding.getId()));
    }

    public BertRustResult embeddings(BertRustEncodingVector vector) {
        return new BertRustResult(LIBRARY.embeddingsCreateInBatch(id, vector.getId()));
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private BertRustModel bean = new BertRustModel();

        private BertRustTokenizer.Builder builder = BertRustTokenizer.builder();

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

        public Builder withMode(String mode) {
            bean.mode = mode;
            return this;
        }

        public BertRustModel build() {
            try {
                Preconditions.checkNotNull(bean.path);
                Preconditions.checkNotNull(bean.mode);
                bean.tokenizer = builder.build();
                bean.id = LIBRARY.modelCreate(bean.gpuId, bean.path.toAbsolutePath().toString(), bean.mode);
                return bean;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

}
