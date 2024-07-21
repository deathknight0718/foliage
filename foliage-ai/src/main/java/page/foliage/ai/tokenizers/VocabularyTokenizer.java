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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import page.foliage.ai.Tokenizer;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.BiMap;
import page.foliage.guava.common.collect.ForwardingMap;
import page.foliage.guava.common.collect.ImmutableBiMap;
import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.guava.common.collect.Lists;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class VocabularyTokenizer implements Tokenizer {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyTokenizer.class);

    private static final int PRINT_SIZE = 64;

    // ------------------------------------------------------------------------

    public static final String LINK = "##";

    public static final long ID_PAD = 0L;

    public static final long ID_UNK = 100L;

    public static final long ID_CLS = 101L;

    public static final long ID_SEP = 102L;

    public static final int MAX_LENGTH = 512;

    // ------------------------------------------------------------------------

    private OrtEnvironment environment = OrtEnvironment.getEnvironment();

    private String delimiter = " ";

    private BiMap<String, Long> index;

    private boolean linked;

    // ------------------------------------------------------------------------

    private VocabularyTokenizer() {}

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    @Override
    public List<String> tokenize(String text) {
        List<String> tokens = Lists.newArrayList();
        tokens.add(index.inverse().get(ID_CLS));
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                tokens.addAll(tokenizeValue(line.toLowerCase()));
                tokens.add(index.inverse().get(ID_SEP));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return tokens;
    }

    public List<String> tokenizeValue(String value) {
        StringBuilder builder = new StringBuilder();
        List<String> tokens = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String token : value.trim().split(delimiter)) {
            char[] chars = token.toCharArray();
            boolean unknown = false;
            int start = 0;
            tokens.clear();
            String current = null;
            while (start < chars.length) {
                int end = chars.length;
                while (start < end) {
                    builder.setLength(0);
                    builder.append(token, start, end);
                    if (start > 0 && linked) builder.insert(0, LINK);
                    String text = builder.toString();
                    if (index.containsKey(text)) {
                        current = text;
                        break;
                    } else current = null;
                    end--;
                }
                if (current == null) {
                    unknown = true;
                    break;
                }
                tokens.add(current);
                start = end;
            }
            if (unknown) result.add(index.inverse().get(ID_UNK));
            else result.addAll(tokens);
        }
        return result;
    }

    @Override
    public Input encode(String text) {
        List<String> tokens = tokenize(text);
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Tokens: {}", StringUtils.abbreviate(tokens.toString(), PRINT_SIZE));
        return Input.scroll(environment, index, tokenize(text));
    }

    // ------------------------------------------------------------------------

    public static class Input extends ForwardingMap<String, OnnxTensor> {

        private Map<String, OnnxTensor> delegate;

        public static Input scroll(OrtEnvironment environment, BiMap<String, Long> index, List<String> tokens) {
            List<long[]> raws = Lists.newArrayList();
            List<Long> temp = Lists.newArrayList();
            List<Long> line = Lists.newArrayList();
            for (String token : tokens) {
                long id = index.get(token);
                if (id == ID_CLS || id == ID_SEP) {
                    if (id == ID_SEP) {
                        temp.add(ID_SEP);
                        if (line.size() + temp.size() >= MAX_LENGTH) {
                            line.add(0, ID_CLS);
                            raws.add(line.stream().mapToLong(Long::longValue).toArray());
                            line.clear();
                        }
                        line.addAll(temp);
                    }
                    temp.clear();
                } else temp.add(id);
            }
            if (!line.isEmpty()) {
                line.add(0, ID_CLS);
                raws.add(line.stream().mapToLong(Long::longValue).toArray());
            }
            long[][] input0 = new long[raws.size()][];
            long[][] input1 = new long[input0.length][];
            long[][] input2 = new long[input0.length][];
            for (int i = 0; i < input0.length; i++) {
                input0[i] = Arrays.copyOf(raws.get(i), MAX_LENGTH);
                input1[i] = Arrays.copyOf(Arrays.stream(raws.get(i)).map(it -> 1).toArray(), MAX_LENGTH);
                input2[i] = Arrays.copyOf(Arrays.stream(raws.get(i)).map(it -> 0).toArray(), MAX_LENGTH);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Token IDs: {}", StringUtils.abbreviate(Arrays.toString(input0[i]), PRINT_SIZE));
                    LOGGER.debug("Attention Mask: {}", StringUtils.abbreviate(Arrays.toString(input1[i]), PRINT_SIZE));
                    LOGGER.debug("Token Type IDs: {}", StringUtils.abbreviate(Arrays.toString(input2[i]), PRINT_SIZE));
                }
            }
            try {
                Input bean = new Input();
                ImmutableMap.Builder<String, OnnxTensor> builder = ImmutableMap.builder();
                builder.put(INPUT_NAMES[0], OnnxTensor.createTensor(environment, input0));
                builder.put(INPUT_NAMES[1], OnnxTensor.createTensor(environment, input1));
                builder.put(INPUT_NAMES[2], OnnxTensor.createTensor(environment, input2));
                bean.delegate = builder.build();
                return bean;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        protected Map<String, OnnxTensor> delegate() {
            return delegate;
        }

    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private VocabularyTokenizer bean = new VocabularyTokenizer();

        public Builder withLinked(boolean linked) {
            bean.linked = linked;
            return this;
        }

        public Builder withDelimiter(String delimiter) {
            bean.delimiter = delimiter;
            return this;
        }

        public Builder withPath(Path path) {
            Preconditions.checkArgument(!Files.notExists(path));
            try (InputStream is = new BufferedInputStream(Files.newInputStream(path))) {
                ImmutableBiMap.Builder<String, Long> builder = ImmutableBiMap.builder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    long i = 0L;
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        String trim = line.trim();
                        if (!trim.isEmpty()) builder.put(trim, i++);
                    }
                }
                bean.index = builder.build();
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public VocabularyTokenizer build() {
            return bean;
        }

    }

}
