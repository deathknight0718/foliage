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
package page.foliage.ai.lucene;

import java.io.IOException;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author deathknight0718@qq.com
 */
public final class JacksonTokenizer extends org.apache.lucene.analysis.Tokenizer {

    // ------------------------------------------------------------------------

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String LINK = ".";

    // ------------------------------------------------------------------------

    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);

    private final TypeAttribute typeAttribute = addAttribute(TypeAttribute.class);

    private JsonParser parser;

    private Stack<String> paths = new Stack<>();

    // ------------------------------------------------------------------------

    public JacksonTokenizer() {}

    // ------------------------------------------------------------------------

    @Override
    public boolean incrementToken() throws IOException {
        if (parser == null) parser = MAPPER.createParser(input);
        while (!parser.isClosed()) {
            parser.nextToken();
            String name = parser.currentName();
            switch (parser.currentTokenId()) {
            case (JsonTokenId.ID_START_OBJECT):
            case (JsonTokenId.ID_START_ARRAY):
                if (StringUtils.isNotEmpty(name)) paths.push(name);
                continue;
            case (JsonTokenId.ID_END_OBJECT):
            case (JsonTokenId.ID_END_ARRAY):
                if (StringUtils.isNotEmpty(name)) paths.pop();
                continue;
            case (JsonTokenId.ID_STRING):
            case (JsonTokenId.ID_NUMBER_INT):
            case (JsonTokenId.ID_NUMBER_FLOAT):
            case (JsonTokenId.ID_FALSE):
            case (JsonTokenId.ID_TRUE):
                clearAttributes();
                if (StringUtils.isNotEmpty(name)) paths.push(name);
                typeAttribute.setType(StringUtils.join(paths, LINK));
                termAttribute.append(parser.getText());
                if (StringUtils.isNotEmpty(name)) paths.pop();
                return true;
            case (JsonTokenId.ID_NO_TOKEN):
                return false;
            }
        }
        return false;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        paths.clear();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (parser != null) parser.close();
    }

}
