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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import page.foliage.common.collect.Identities;
import page.foliage.guava.common.io.Resources;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class CandleLibrary {

    // ------------------------------------------------------------------------

    public static final String RESOURCE_NAME = "libfoliageai.so";

    public static final String RESOURCE_URL_TEXT = "native/lib/linux-x86-64/" + RESOURCE_NAME;

    // ------------------------------------------------------------------------

    private static volatile CandleLibrary INSTANCE;

    // ------------------------------------------------------------------------

    private CandleLibrary() {}

    // ------------------------------------------------------------------------

    public static CandleLibrary instance() {
        CandleLibrary result = INSTANCE;
        if (result == null) {
            synchronized (CandleLibrary.class) {
                result = INSTANCE;
                if (result == null) {
                    try (InputStream in = Resources.getResource(RESOURCE_URL_TEXT).openStream()) {
                        File temp = File.createTempFile(Identities.uuid().toString(), RESOURCE_NAME);
                        temp.deleteOnExit();
                        Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        System.load(temp.getAbsolutePath());
                        result = INSTANCE = new CandleLibrary();
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------

    public native boolean isCudaAvailable();

    // ------------------------------------------------------------------------

    public native long tokenizerCreate(String json);

    public native void tokenizerDelete(long tokenizerId);

    public native int tokenizerMaxLength(long tokenizerId);

    public native int tokenizerStride(long tokenizerId);

    public native int tokenizerPadToMultipleOf(long tokenizerId);

    // ------------------------------------------------------------------------

    public native String tokenizerPaddingStrategy(long tokenizerId);

    public native void tokenizerPaddingDisable(long tokenizerId);

    public native void tokenizerPaddingUpdate(long tokenizerId, int maxLength, String strategy, int padToMultipleOf);

    // ------------------------------------------------------------------------

    public native String tokenizerTruncationStrategy(long tokenizer);

    public native void tokenizerTruncationUpdate(long tokenizerId, int maxLength, String strategy, int stride);

    public native void tokenizerTruncationDisable(long tokenizerId);

    // ------------------------------------------------------------------------

    public native long encodingCreate(long tokenizerId, String text, boolean addSpecialTokens);

    public native void encodingDelete(long encodingId);

    // ------------------------------------------------------------------------

    public native String[] tokensGet(long encodingId);

    public native long[] tokenIdsGet(long encodingId);

    public native long[] tokenTypeIdsGet(long encodingId);

    public native long[] tokenWordIdsGet(long encodingId);

    public native long[] attentionMaskGet(long encodingId);

    public native long[] specialTokenMaskGet(long encodingId);

    // ------------------------------------------------------------------------

    public native String decode(long tokenizerId, long[] tokenIds, boolean addSpecialTokens);

    // ------------------------------------------------------------------------
    
    public native long modelCreate(int gpuId, String path);
    
    public native void modelDelete(long modelId);
    
    public native long embeddingsCreate(long modelId, long encodingId);
    
    public native void embeddingsDelete(long embeddingsId);
    
    public native Object embeddingsLastHiddenState(long embeddingsId);
    
    public native Object embeddings(long embeddingsId);

}
