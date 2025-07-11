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

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestBertNative {

    private static Path tpath = Paths.get("/home/foliage/model/Phi-3-mini-4k-instruct/tokenizer.json");

    private static BertRustLibrary library;

    @BeforeClass
    public static void beforeClass() {
        library = BertRustLibrary.instance();
    }

    @Test
    private void test1() throws Exception {
        long tokenizerId = library.tokenizerCreate(tpath.toString());
        Assert.assertTrue(tokenizerId > 0L);
        Assert.assertTrue(library.isCudaAvailable());
        System.err.println(library.tokenizerMaxLength(tokenizerId));
        library.tokenizerPaddingUpdate(tokenizerId, 384, "MAX_LENGTH", 0);
        library.tokenizerTruncationUpdate(tokenizerId, 384, "LONGEST_FIRST", 0);
        System.err.println(library.tokenizerPaddingStrategy(tokenizerId));
        System.err.println(library.tokenizerTruncationStrategy(tokenizerId));
        System.err.println(library.tokenizerMaxLength(tokenizerId));
        System.err.println(library.tokenizerStride(tokenizerId));
        System.err.println(library.tokenizerPadToMultipleOf(tokenizerId));
        long encodingId = library.encodingCreate(tokenizerId, "床前明月光", true);
        System.err.println(Arrays.toString(library.tokensGet(encodingId)));
        System.err.println(Arrays.toString(library.tokenIdsGet(encodingId)));
        System.err.println(Arrays.toString(library.tokenTypeIdsGet(encodingId)));
        System.err.println(Arrays.toString(library.tokenWordIdsGet(encodingId)));
        System.err.println(Arrays.toString(library.attentionMaskGet(encodingId)));
        System.err.println(Arrays.toString(library.specialTokenMaskGet(encodingId)));
        System.err.println(library.decode(tokenizerId, library.tokenIdsGet(encodingId), true));
        library.encodingDelete(encodingId);
        library.tokenizerDelete(tokenizerId);
    }

    @Test
    private void test2() throws Exception {
        long modelId = library.modelCreate(0, "/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2", BertRustModel.MODE_PT);
        long tokenizerId = library.tokenizerCreate(tpath.toString());
        long encodingId = library.encodingCreate(tokenizerId, "床前明月光", true);
        long embeddingsId = library.embeddingsCreate(modelId, encodingId);
        float[][] embeddings = (float[][]) library.embeddings(embeddingsId);
        System.err.println(Arrays.toString(embeddings[0]));
        library.embeddingsDelete(embeddingsId);
        library.encodingDelete(encodingId);
        library.tokenizerDelete(tokenizerId);
        library.modelDelete(modelId);
    }

}
