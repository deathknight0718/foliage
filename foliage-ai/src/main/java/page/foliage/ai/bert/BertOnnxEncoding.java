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

import java.util.Map;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import page.foliage.guava.common.collect.ForwardingMap;
import page.foliage.guava.common.collect.ImmutableMap;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class BertOnnxEncoding extends ForwardingMap<String, OnnxTensor> implements BertEncoding {

    // ------------------------------------------------------------------------

    private static final String[] INPUT_NAMES = new String[] { "input_ids", "attention_mask", "token_type_ids" };

    private final static OrtEnvironment environment = OrtEnvironment.getEnvironment();

    private Map<String, OnnxTensor> delegate;

    private String[] tokens;

    private long[] tokenIds, tokenTypeIds, attentionMask;

    // ------------------------------------------------------------------------

    private BertOnnxEncoding() {}

    // ------------------------------------------------------------------------

    public static BertOnnxEncoding create(BertEncoding encoding) throws OrtException {
        BertOnnxEncoding bean = new BertOnnxEncoding();
        bean.tokens = encoding.getTokens();
        bean.tokenIds = encoding.getTokenIds();
        bean.tokenTypeIds = encoding.getTokenTypeIds();
        bean.attentionMask = encoding.getAttentionMask();
        ImmutableMap.Builder<String, OnnxTensor> builder = ImmutableMap.builder();
        builder.put(INPUT_NAMES[0], OnnxTensor.createTensor(environment, new long[][] { bean.tokenIds }));
        builder.put(INPUT_NAMES[1], OnnxTensor.createTensor(environment, new long[][] { bean.attentionMask }));
        builder.put(INPUT_NAMES[2], OnnxTensor.createTensor(environment, new long[][] { bean.tokenTypeIds }));
        bean.delegate = builder.build();
        return bean;
    }

    // ------------------------------------------------------------------------

    @Override
    protected Map<String, OnnxTensor> delegate() {
        return delegate;
    }

    // ------------------------------------------------------------------------

    @Override
    public String[] getTokens() {
        return tokens;
    }

    @Override
    public long[] getTokenIds() {
        return tokenIds;
    }

    @Override
    public long[] getTokenTypeIds() {
        return tokenTypeIds;
    }

    @Override
    public long[] getAttentionMask() {
        return attentionMask;
    }

}
