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
package page.foliage.ai;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import page.foliage.ai.candle.CandleEncoding;
import page.foliage.ai.candle.CandleModel;
import page.foliage.ai.candle.CandleResult;
import page.foliage.ai.candle.CandleTokenizer;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestCandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCandle.class);

    private static Path path = Paths.get("/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2");

    @Test
    private void test1() throws Exception {
        try (CandleTokenizer tokenizer = CandleTokenizer.builder().withPath(path).build()) {
            try (CandleEncoding encoding = tokenizer.encode("床前明月光")) {
                LOGGER.info(Arrays.toString(encoding.getTokens()));
            }
        }
    }

    @Test
    private void test2() throws Exception {
        try (CandleModel model = CandleModel.builder().withPath(path).withGpuId(0).build()) {
            try (CandleResult result = model.embeddings("床前明月光")) {
                LOGGER.info(Arrays.toString(result.lastHiddenState()[0][0]));
                LOGGER.info(Arrays.toString(result.embeddings()[0]));
                LOGGER.info(Arrays.toString(Functions.mean(result.lastHiddenState(), 1)[0]));
            }
        }
    }

}
