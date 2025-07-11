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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import page.foliage.common.util.JsonNodes;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestIntent {

    private static Path path = Paths.get("/home/foliage/fork/Huatuo26M-Lite/format_data.jsonl");

    @Test
    private void testLucene() throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        try (Directory directory = new ByteBuffersDirectory()) {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            try (IndexWriter writer = new IndexWriter(directory, config)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        Document document = new Document();
                        JsonNode node = JsonNodes.asNode(line);
                        document.add(new TextField("question", node.path("question").asText(), Field.Store.YES));
                        document.add(new TextField("answer", node.path("answer").asText(), Field.Store.YES));
                        document.add(new KnnFloatVectorField("vector", null, VectorSimilarityFunction.COSINE));
                        writer.addDocument(document);
                    }
                }
            }
            try (DirectoryReader reader = DirectoryReader.open(directory)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                QueryParser parser = new QueryParser("answer", analyzer);
                Query query = parser.parse("我得了糖尿病，应该去哪一家医院就诊");
                TopDocs docs = searcher.search(query, 10);
                System.out.println("Total Hits: " + docs.totalHits.value);
                for (ScoreDoc hit : docs.scoreDocs) {
                    StoredFields fields = searcher.storedFields();
                    System.out.println("DocID: " + hit.doc + ", Score: " + hit.score);
                    System.out.println("Content: " + fields.document(hit.doc).get("answer"));
                }
            }
        }
    }

}
