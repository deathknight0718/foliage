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

import java.nio.file.Path;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class BertOnnxTokenizer implements BertTokenizer, AutoCloseable {

    // ------------------------------------------------------------------------

    private BertRustTokenizer delegate;

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String[] tokenize(String text) {
        return delegate.tokenize(text);
    }

    @Override
    public BertOnnxEncoding encode(String text) {
        try (BertRustEncoding encoding = delegate.encode(text)) {
            return BertOnnxEncoding.create(encoding);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        delegate.close();
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private BertRustTokenizer.Builder builder = BertRustTokenizer.builder();

        public Builder withPath(Path path) {
            builder.withPath(path);
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

        public BertOnnxTokenizer build() {
            BertOnnxTokenizer bean = new BertOnnxTokenizer();
            try {
                bean.delegate = builder.build();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            return bean;
        }

    }

}
