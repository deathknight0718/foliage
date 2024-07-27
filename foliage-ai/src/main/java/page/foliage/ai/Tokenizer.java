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

import java.util.List;
import java.util.Map;

import ai.onnxruntime.OnnxTensor;

/**
 * 
 * @author deathknight0718@qq.com
 */
public interface Tokenizer {

    String[] INPUT_NAMES = new String[] { "input_ids", "attention_mask", "token_type_ids" };

    List<String> tokenize(String text);

    Map<String, OnnxTensor> encode(String text);

}