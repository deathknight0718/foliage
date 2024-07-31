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

import java.nio.file.Path;

import page.foliage.ai.Tokenizer;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class CandleTokenizer implements Tokenizer, AutoCloseable {

    // ------------------------------------------------------------------------

    private final static CandleLibrary LIBRARY = CandleLibrary.instance();

    private long id;

    private Path path;

    private boolean padding, truncation;

    // ------------------------------------------------------------------------

    private CandleTokenizer() {}

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        LIBRARY.tokenizerDelete(id);
    }

    // ------------------------------------------------------------------------

    @Override
    public String[] tokenize(String text) {
        return encode(text).getTokens();
    }

    @Override
    public CandleEncoding encode(String text) {
        return new CandleEncoding(LIBRARY.encodingCreate(id, text, true));
    }

    // ------------------------------------------------------------------------

    public long getId() {
        return id;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private CandleTokenizer bean = new CandleTokenizer();

        public Builder withPath(Path path) {
            bean.path = path.resolve("tokenizer.json").toAbsolutePath();
            return this;
        }

        public Builder withPadding(boolean padding) {
            bean.padding = padding;
            return this;
        }

        public Builder withTruncation(boolean truncation) {
            bean.truncation = truncation;
            return this;
        }

        public CandleTokenizer build() {
            try {
                bean.id = LIBRARY.tokenizerCreate(bean.path.toString());
                int maxLength = LIBRARY.tokenizerMaxLength(bean.id);
                if (bean.padding) LIBRARY.tokenizerPaddingUpdate(bean.id, maxLength, "", 0);
                if (bean.truncation) LIBRARY.tokenizerTruncationUpdate(bean.id, maxLength, null, 0);
                return bean;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

}
