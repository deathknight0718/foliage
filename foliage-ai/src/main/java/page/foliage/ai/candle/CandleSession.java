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
package page.foliage.ai.candle;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import page.foliage.ai.ModelSession;
import page.foliage.ai.Result;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class CandleSession implements ModelSession {

    // ------------------------------------------------------------------------

    private final CandleModel model;

    // ------------------------------------------------------------------------

    public CandleSession(CandleModel model) {
        this.model = model;
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        model.close();
    }

    @Override
    public Result run(File file) throws Exception {
        return run(Files.readString(file.toPath(), StandardCharsets.UTF_8));
    }

    @Override
    public Result run(String text) throws Exception {
        return model.embeddings(text);
    }
    
    public Result run(String[] texts) throws Exception {
        return model.embeddings(texts);
    }

}
