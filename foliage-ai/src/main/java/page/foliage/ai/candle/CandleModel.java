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
    }

    // ------------------------------------------------------------------------

    public CandleTensor embeddings(String text) throws Exception {
        try (CandleEncoding encoding = tokenizer.encode(text)) {
            return embeddings(encoding);
        }
    }

    public CandleTensor embeddings(CandleEncoding encoding) {
        return new CandleTensor(LIBRARY.embeddingsCreate(id, encoding.getId()));
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private CandleModel bean = new CandleModel();

        public Builder withPath(Path path) {
            bean.path = path;
            return this;
        }

        public Builder withFile(File file) {
            bean.path = file.toPath();
            return this;
        }

        public Builder withGpuId(int gpuId) {
            bean.gpuId = gpuId;
            return this;
        }

        public Builder withTokenizer(CandleTokenizer tokenizer) {
            bean.tokenizer = tokenizer;
            return this;
        }

        public CandleModel build() {
            try {
                Preconditions.checkArgument(Files.isDirectory(bean.path));
                bean.id = LIBRARY.modelCreate(bean.gpuId, bean.path.toAbsolutePath().toString());
                return bean;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

}
