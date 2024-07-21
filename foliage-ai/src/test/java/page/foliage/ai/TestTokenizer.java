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

import org.testng.annotations.Test;

import page.foliage.ai.tokenizers.NativeTokenizer;
import page.foliage.ai.tokenizers.VocabularyTokenizer;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestTokenizer {

    private static Path path1 = Paths.get("/home/foliage/model/bert-base-chinese/vocab.txt");

    private static Path path2 = Paths.get("/home/foliage/model/Phi-3-mini-4k-instruct/tokenizer.json");

    @Test
    private void test1() throws Exception {
        String text = "这是一份 FHIR 档案, 患者全名为 Tad831 Laverne101, 性别为 MALE, 出生日期是 1994-06-28.\\n患者联系方式为 HOME:PHONE:555-152-2603, 家庭住址为 235 Hermann Ville, Brookfield, MA, US.\\n家庭住址为 235 Hermann Ville, Brookfield, MA, US.\\n";
        VocabularyTokenizer tokenizer = VocabularyTokenizer.builder().withPath(path1).build();
        System.err.println(tokenizer.tokenize(text));
    }

    @Test
    private void test2() throws Exception {
        String text = "这是一份 FHIR 档案, 患者全名为 Tad831 Laverne101, 性别为 MALE, 出生日期是 1994-06-28.\n患者联系方式为 HOME:PHONE:555-152-2603, 家庭住址为 235 Hermann Ville, Brookfield, MA, US.\n家庭住址为 235 Hermann Ville, Brookfield, MA, US.\n";
        NativeTokenizer tokenizer = NativeTokenizer.builder().withPath(path2).build();
        System.err.println(tokenizer.tokenize(text));
    }

}
