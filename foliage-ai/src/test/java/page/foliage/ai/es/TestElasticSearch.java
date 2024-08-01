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
package page.foliage.ai.es;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import page.foliage.ai.Intent;
import page.foliage.ai.bert.BertModelSession;
import page.foliage.ai.bert.BertOnnxSessionFactory;
import page.foliage.ai.bert.BertResult;
import page.foliage.common.collect.Identities;
import page.foliage.common.ioc.InstanceClosingCheck;
import page.foliage.common.util.JsonNodes;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestElasticSearch {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TestElasticSearch.class);

    private static final String ENDPOINT = "https://portal.cecdat.dev:9200";

    private static final String BASIC_ACCESS_TOKEN = "ZWxhc3RpYzpjaGFuZ2VpdA==";

    private static Path path = Paths.get("/home/foliage/fork/Huatuo26M-Lite/format_data.jsonl");

    private static Path mpath = Paths.get("/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2");

    private static Path spath = Paths.get("target/test-classes/searchs/hybird-intents.json");

    private static ElasticsearchClient client;

    private static BertOnnxSessionFactory factory;

    @BeforeClass
    public static void beforeClass() {
        RestClient rest = RestClient.builder(HttpHost.create(ENDPOINT)) //
            .setDefaultHeaders(new Header[] { new BasicHeader("Authorization", "Basic " + BASIC_ACCESS_TOKEN) }) //
            .build();
        ElasticsearchTransport transport = new RestClientTransport(rest, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
        BertOnnxSessionFactory.Builder builder = BertOnnxSessionFactory.builder();
        factory = builder.withDirectory(mpath).build();
        InstanceClosingCheck.hook(factory);
    }

    @Test
    private void testIndex() throws Exception {
        try (BertModelSession session = factory.openSession(0)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    JsonNode node = JsonNodes.asNode(line);
                    Intent.Builder builder = Intent.builder();
                    builder.withId(Identities.uuid());
                    builder.withInstruction(node.path("question").asText());
                    builder.withContent(node.path("answer").asText());
                    try (BertResult result = session.run(node.path("question").asText())) {
                        builder.withInstructionEmbeddings(result.embeddings()[0]);
                    }
                    try (BertResult result = session.run(node.path("answer").asText())) {
                        builder.withContentEmbeddings(result.embeddings()[0]);
                    }
                    Intent bean = builder.build();
                    System.out.println(bean.getContentEmbeddings().length);
                    client.index(i -> i.index("intents-1").id(bean.getId().toString()).document(bean));
                }
            }
        }
    }

    @Test
    private void testSearch() throws Exception {
        String input = "我想找一家治疗糖尿病比较好的医院";
        try (BertModelSession session = factory.openSession(0)) {
            try (BertResult result = session.run(input)) {
                float[] embeddings = result.embeddings()[0];
                ObjectNode node = JsonNodes.asObject(Files.readString(spath));
                ((ObjectNode) node.at(JsonPointer.compile("/knn/filter/bool/must/multi_match"))).set("query", TextNode.valueOf(input));
                ((ObjectNode) node.at(JsonPointer.compile("/knn"))).set("query_vector", JsonNodes.asArray(embeddings));
                SearchRequest.Builder builder = new SearchRequest.Builder();
                try (InputStream is = new ByteArrayInputStream(node.toString().getBytes())) {
                    SearchRequest request = builder.index("intents-1").withJson(is).build();
                    SearchResponse<Intent> response = client.search(request, Intent.class);
                    for (Hit<Intent> hit : response.hits().hits()) {
                        LOGGER.info("hit {}", hit);
                    }
                }
            }
        }
    }

}
