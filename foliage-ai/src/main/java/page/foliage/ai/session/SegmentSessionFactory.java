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
package page.foliage.ai.session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import page.foliage.ai.Result;
import page.foliage.ai.func.SpaceFunctions;
import page.foliage.common.util.ResourceUtils;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.ImmutableMap;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class SegmentSessionFactory implements AutoCloseable {

    // ------------------------------------------------------------------------

    private Path path;

    private OrtEnvironment environment = OrtEnvironment.getEnvironment();

    private volatile PooledSession sessionPooled;

    // ------------------------------------------------------------------------

    private SegmentSessionFactory() {}

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        if (sessionPooled != null) sessionPooled.close();
    }

    // ------------------------------------------------------------------------

    public SegmentSession openPooledSession(int gpuId) throws Exception {
        PooledSession result = sessionPooled;
        if (result == null) {
            synchronized (this) {
                if ((result = sessionPooled) == null) sessionPooled = result = new PooledSession(openSession(gpuId));
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------

    public SegmentSession openSession(int gpuId) throws Exception {
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        options.addCUDA(0);
        OrtSession session = environment.createSession(path.toString(), options);
        return new InternalSession(options, session);
    }

    public SegmentSession openSession() throws Exception {
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        OrtSession session = environment.createSession(path.toString(), options);
        return new InternalSession(options, session);
    }

    // ------------------------------------------------------------------------

    public class PooledSession implements SegmentSession {

        private final SegmentSession delegate;

        public PooledSession(SegmentSession delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() throws Exception {}

        @Override
        public Result run(Path path) throws Exception {
            return delegate.run(path);
        }

    }

    // ------------------------------------------------------------------------

    public class InternalSession implements SegmentSession {

        private final OrtSession.SessionOptions options;

        private final OrtSession delegate;

        public InternalSession(OrtSession.SessionOptions options, OrtSession delegate) {
            this.options = options;
            this.delegate = delegate;
        }

        @Override
        public void close() throws Exception {
            ResourceUtils.safeClose(delegate, options);
        }

        public InternalResult run(Path path) throws OrtException, IOException {
            Preconditions.checkArgument(Files.isReadable(path));
            Mat rgb = new Mat();
            Imgproc.cvtColor(Imgcodecs.imread(path.toString()), rgb, Imgproc.COLOR_BGR2RGB);
            ImmutableMap.Builder<String, OnnxTensor> builder = ImmutableMap.builder();
            builder.put("image_embeddings", null);
            builder.put("point_coords", null);
            builder.put("point_labels", null);
            builder.put("mask_input", null);
            builder.put("has_mask_input", null);
            builder.put("orig_im_size", null);
            return run(builder.build());
        }

        public InternalResult run(Map<String, OnnxTensor> input) throws OrtException {
            return new InternalResult(delegate.run(input));
        }

    }

    // ------------------------------------------------------------------------

    public static class InternalResult implements Result {

        private final ai.onnxruntime.OrtSession.Result delegate;

        public InternalResult(ai.onnxruntime.OrtSession.Result delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() throws Exception {
            delegate.close();
        }

        @Override
        public float[][][] lastHiddenState() throws Exception {
            return (float[][][]) delegate.get("last_hidden_state").get().getValue();
        }

        @Override
        public float[][] embeddings() throws Exception {
            return SpaceFunctions.mean(lastHiddenState(), 1);
        }

    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private SegmentSessionFactory bean = new SegmentSessionFactory();

        public Builder withModel(Path path) {
            bean.path = path;
            return this;
        }

        public SegmentSessionFactory build() {
            return bean;
        }

    }

}
