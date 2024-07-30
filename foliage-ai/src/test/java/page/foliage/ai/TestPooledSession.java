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

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import page.foliage.ai.ort.OrtSessionFactory;
import page.foliage.common.collect.Identities;
import page.foliage.common.ioc.InstanceClosingCheck;
import page.foliage.common.util.JsonNodes;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestPooledSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestPooledSession.class);

    private static Path path = Paths.get("/home/foliage/fork/Huatuo26M-Lite/format_data.jsonl");

    private static Path mpath = Paths.get("/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2");

    private static OrtSessionFactory factory;

    @BeforeClass
    public static void beforeClass() {
        OrtSessionFactory.Builder builder = OrtSessionFactory.builder();
        factory = builder.withDirectory(mpath).build();
        InstanceClosingCheck.hook(factory);
    }

    @Test
    public void testMulti() {
        Multi.createFrom().items(1, 2, 3, 4, 5, 6) //
            .onItem() //
            .transformToUniAndMerge(i -> Uni.createFrom().item(() -> consume(i)).runSubscriptionOn(Infrastructure.getDefaultExecutor())) //
            .collect().asList().await().indefinitely();
    }

    private static boolean consume(int i) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                JsonNode node = JsonNodes.asNode(line);
                Intent.Builder builder = Intent.builder();
                builder.withId(Identities.uuid());
                builder.withInstruction(node.path("question").asText());
                builder.withContent(node.path("answer").asText());
                try (ModelSession session = factory.openPooledSession(0)) {
                    LOGGER.info("pooled session opened");
                    try (Result result = session.run(node.path("question").asText())) {
                        builder.withInstructionEmbeddings(result.embeddings()[0]);
                    }
                    try (Result result = session.run(node.path("answer").asText())) {
                        builder.withContentEmbeddings(result.embeddings()[0]);
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
