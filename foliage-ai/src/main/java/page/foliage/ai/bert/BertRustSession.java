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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class BertRustSession implements BertModelSession {

    // ------------------------------------------------------------------------

    private final BertRustModel model;

    // ------------------------------------------------------------------------

    public BertRustSession(BertRustModel model) {
        this.model = model;
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        model.close();
    }

    @Override
    public BertResult run(File file) throws Exception {
        return run(Files.readString(file.toPath(), StandardCharsets.UTF_8));
    }

    @Override
    public BertResult run(String text) throws Exception {
        return model.embeddings(text);
    }
    
    public BertResult run(String[] texts) throws Exception {
        return model.embeddings(texts);
    }

}
