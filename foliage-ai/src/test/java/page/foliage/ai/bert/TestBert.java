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
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import page.foliage.ai.Functions;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestBert {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestBert.class);

    private final static Path PATH_MODEL = Paths.get("/home/foliage/models/paraphrase-multilingual-MiniLM-L12-v2");

    @Test
    private void test1() throws Exception {
        try (BertRustTokenizer tokenizer = BertRustTokenizer.builder().withPath(PATH_MODEL).build()) {
            try (BertRustEncoding encoding = tokenizer.encode("床前明月光")) {
                LOGGER.info(Arrays.toString(encoding.getTokens()));
            }
        }
    }

    @Test
    private void test2() throws Exception {
        try (BertRustModel model = BertRustModel.builder().withPath(PATH_MODEL).withGpuId(0).build()) {
            try (BertRustResult result = model.embeddings("床前明月光")) {
                LOGGER.info("lastHiddenState [0][0]: {}", Arrays.toString(result.lastHiddenState()[0][0]));
                LOGGER.info("embeddings [0]: {}", Arrays.toString(result.embeddings()[0]));
                LOGGER.info("java embeddings [0]: {}", Arrays.toString(Functions.mean(result.lastHiddenState(), 1)[0]));
            }
            try (BertRustResult result = model.embeddings("举头望明月")) {
                LOGGER.info("lastHiddenState [0][0]: {}", Arrays.toString(result.lastHiddenState()[0][0]));
                LOGGER.info("embeddings [0]: {}", Arrays.toString(result.embeddings()[0]));
                LOGGER.info("java embeddings [0]: {}", Arrays.toString(Functions.mean(result.lastHiddenState(), 1)[0]));
            }
            try (BertRustResult result = model.embeddings(new String[] { "床前明月光", "举头望明月" })) {
                LOGGER.info("lastHiddenState [0][0]: {}", Arrays.toString(result.lastHiddenState()[0][0]));
                LOGGER.info("embeddings [0]: {}", Arrays.toString(result.embeddings()[0]));
                LOGGER.info("embeddings [1]: {}", Arrays.toString(result.embeddings()[1]));
                LOGGER.info("java embeddings [0]: {}", Arrays.toString(Functions.mean(result.lastHiddenState(), 1)[0]));
                LOGGER.info("java embeddings [1]: {}", Arrays.toString(Functions.mean(result.lastHiddenState(), 1)[1]));
            }
        }
    }

}
