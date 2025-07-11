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

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import page.foliage.common.ioc.InstanceClosingCheck;
import page.foliage.common.util.JsonNodes;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestBertPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestBertPool.class);

    private static Path path = Paths.get("/home/foliage/fork/Huatuo26M-Lite/format_data.jsonl");

    private static Path mpath = Paths.get("/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2");

    private static BertRustSessionFactory factory1;

    private static BertOnnxSessionFactory factory2;

    @BeforeClass
    public static void beforeClass() {
        factory1 = BertRustSessionFactory.builder().withPath(mpath).withGpuId(0).withMode(BertRustModel.MODE_ST).build();
        InstanceClosingCheck.hook(factory1);
        factory2 = BertOnnxSessionFactory.builder().withDirectory(mpath).build();
        InstanceClosingCheck.hook(factory2);
    }

    @Test
    public void testMulti1() {
        Multi.createFrom().items(1, 2, 3, 4, 5, 6) //
            .onItem() //
            .transformToUniAndMerge(i -> Uni.createFrom().item(() -> consume1(i)).runSubscriptionOn(Infrastructure.getDefaultExecutor())) //
            .collect().asList().await().indefinitely();
    }

    private static boolean consume1(int i) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line = null;
            int index = 0;
            while ((line = reader.readLine()) != null && index < 100) {
                JsonNode node = JsonNodes.asNode(line);
                try (BertModelSession session = factory1.openPooledSession(0)) {
                    try (BertResult result = session.run(node.path("question").asText())) {
                        LOGGER.info("answer result size: {}", Arrays.toString(result.embeddings()[0]));
                    }
                    try (BertResult result = session.run(node.path("answer").asText())) {
                        LOGGER.info("answer result size: {}", Arrays.toString(result.embeddings()[0]));
                    }
                }
                index++;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Test
    public void testMulti2() {
        Multi.createFrom().items(1, 2, 3, 4, 5, 6) //
            .onItem() //
            .transformToUniAndMerge(i -> Uni.createFrom().item(() -> consume2(i)).runSubscriptionOn(Infrastructure.getDefaultExecutor())) //
            .collect().asList().await().indefinitely();
    }

    private static boolean consume2(int i) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line = null;
            int index = 0;
            while ((line = reader.readLine()) != null && index < 100) {
                JsonNode node = JsonNodes.asNode(line);
                try (BertModelSession session = factory2.openPooledSession(0)) {
                    try (BertResult result = session.run(node.path("question").asText())) {
                        LOGGER.info("answer result size: {}", Arrays.toString(result.embeddings()[0]));
                    }
                    try (BertResult result = session.run(node.path("answer").asText())) {
                        LOGGER.info("answer result size: {}", Arrays.toString(result.embeddings()[0]));
                    }
                }
                index++;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
