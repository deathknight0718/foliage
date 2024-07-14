/*
 * Copyright 2023 Foliage Develop Team.
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
package page.foliage.flow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import page.foliage.common.util.JsonNodes;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FormPayload {

    // ------------------------------------------------------------------------

    public static final String KEYWORD_SCHEMA = "schema";

    public static final String KEYWORD_UI_SCHEMA = "uiSchema";

    public static final String KEYWORD_FORM_DATA = "formData";

    public static final String KEYWORD_ACCESS = "access";

    // ------------------------------------------------------------------------

    private final JsonNode delegate;

    // ------------------------------------------------------------------------

    public FormPayload(JsonNode delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------

    public static FormPayload of(String json) {
        try {
            return new FormPayload(JsonNodes.asNode(json));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------------------------

    public FlowVariables variables() {
        JsonNode data = delegate.path(KEYWORD_FORM_DATA);
        if (!data.isObject()) return FlowVariables.EMPTY;
        return FlowVariables.of(data);
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return delegate.toString();
    }

}
