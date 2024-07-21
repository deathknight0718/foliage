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

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Intent {

    public static final double LIMIT = 0.7D;

    // ------------------------------------------------------------------------

    private UUID id;

    private String instruction, content;

    private float[] instructionEmbeddings, contentEmbeddings;

    // ------------------------------------------------------------------------

    private Intent() {}

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    public UUID getId() {
        return id;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getContent() {
        return content;
    }

    @JsonProperty("instruction_embeddings")
    public float[] getInstructionEmbeddings() {
        return instructionEmbeddings;
    }

    @JsonProperty("content_embeddings")
    public float[] getContentEmbeddings() {
        return contentEmbeddings;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private Intent bean = new Intent();

        public Builder withId(UUID id) {
            bean.id = id;
            return this;
        }

        public Builder withInstruction(String instruction) {
            bean.instruction = instruction;
            return this;
        }

        public Builder withInstructionEmbeddings(float[] embeddings) {
            bean.instructionEmbeddings = embeddings;
            return this;
        }

        public Builder withContent(String content) {
            bean.content = content;
            return this;
        }

        public Builder withContentEmbeddings(float[] embeddings) {
            bean.contentEmbeddings = embeddings;
            return this;
        }

        public Intent build() {
            return bean;
        }

    }

}
