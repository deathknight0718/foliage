/*
 * Copyright 2022 Deathknight0718.
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
package page.foliage.oidc;

import java.io.IOException;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class OidcClient implements Cloneable, Call.Factory, WebSocket.Factory {

    // ------------------------------------------------------------------------

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private final OkHttpClient delegate;

    // ------------------------------------------------------------------------

    private OidcClient(OkHttpClient delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    @Override
    public WebSocket newWebSocket(Request request, WebSocketListener listener) {
        return delegate.newWebSocket(request, listener);
    }

    @Override
    public Call newCall(Request request) {
        return delegate.newCall(request);
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private OkHttpClient.Builder bean = new OkHttpClient.Builder();

        private ClientCredentialsInterceptor interceptor = new ClientCredentialsInterceptor();

        public Builder oidc(String url) {
            interceptor.url = url;
            return this;
        }

        public Builder credentials(String id, String secret) {
            interceptor.id = id;
            interceptor.secret = secret;
            return this;
        }

        public OidcClient build() {
            bean.addInterceptor(interceptor);
            bean.callTimeout(30, TimeUnit.SECONDS);
            bean.retryOnConnectionFailure(false);
            return new OidcClient(bean.build());
        }

    }

    // ------------------------------------------------------------------------

    public static class ClientCredentialsInterceptor implements Interceptor {

        private String url, id, secret;

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = token(chain);
            if (!response.isSuccessful()) return response;
            JsonNode body = MAPPER.readTree(response.body().byteStream());
            StringJoiner joiner = new StringJoiner(" ");
            joiner.add(body.get("token_type").asText());
            joiner.add(body.get("access_token").asText());
            request.newBuilder().addHeader("Authorization", joiner.toString());
            return chain.proceed(request);
        }

        public Response token(Chain chain) throws IOException {
            FormBody.Builder form = new FormBody.Builder();
            form.add("client_id", id);
            form.add("client_secret", secret);
            form.add("grant_type", "client_credentials");
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(form.build());
            return chain.proceed(builder.build());
        }

    }

}
