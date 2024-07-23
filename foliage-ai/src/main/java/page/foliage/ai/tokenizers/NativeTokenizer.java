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
package page.foliage.ai.tokenizers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import page.foliage.ai.Tokenizer;
import page.foliage.guava.common.collect.ImmutableMap;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class NativeTokenizer implements Tokenizer {

    // ------------------------------------------------------------------------

    private HuggingFaceTokenizer delegate;

    private OrtEnvironment environment = OrtEnvironment.getEnvironment();

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public List<String> tokenize(String text) {
        return delegate.tokenize(text);
    }

    @Override
    public Map<String, OnnxTensor> encode(String text) {
        Encoding encoding = delegate.encode(text);
        try {
            ImmutableMap.Builder<String, OnnxTensor> builder = ImmutableMap.builder();
            builder.put(INPUT_NAMES[0], OnnxTensor.createTensor(environment, new long[][] { encoding.getIds() }));
            builder.put(INPUT_NAMES[1], OnnxTensor.createTensor(environment, new long[][] { encoding.getAttentionMask() }));
            builder.put(INPUT_NAMES[2], OnnxTensor.createTensor(environment, new long[][] { encoding.getTypeIds() }));
            return builder.build();
        } catch (OrtException e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private HuggingFaceTokenizer.Builder builder = HuggingFaceTokenizer.builder();

        public Builder withPath(Path path) {
            builder.optTokenizerPath(path);
            return this;
        }

        public Builder withPadding(boolean padding) {
            builder.optPadding(padding);
            return this;
        }

        public Builder withTruncation(boolean truncation) {
            builder.optTruncation(truncation);
            return this;
        }

        public NativeTokenizer build() {
            NativeTokenizer bean = new NativeTokenizer();
            try {
                bean.delegate = builder.build();
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            return bean;
        }

    }

}
