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
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.oidc.OidcConfiguration.GrantType;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class OidcClient implements Cloneable, Call.Factory, WebSocket.Factory {

    // ------------------------------------------------------------------------

    private final static Logger LOGGER = LoggerFactory.getLogger(OidcClient.class);

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private final OkHttpClient delegate;

    private HttpUrl baseUrl;

    // ------------------------------------------------------------------------

    private OidcClient(OkHttpClient delegate, HttpUrl baseUrl) {
        this.delegate = delegate;
        this.baseUrl = baseUrl;
    }

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    public okhttp3.HttpUrl.Builder httpUrl() {
        return baseUrl.newBuilder();
    }
    
    public okhttp3.HttpUrl.Builder httpUrl(String link) {
        return baseUrl.newBuilder(link);
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

        private OidcInterceptor interceptor = new OidcInterceptor();

        private HttpUrl baseUrl;

        public Builder base(String baseUrl) {
            this.baseUrl = HttpUrl.get(baseUrl);
            return this;
        }

        public Builder oidc(OidcConfiguration configuration) {
            interceptor.configuration = configuration;
            return this;
        }

        public Builder callTimeout(long timeout, TimeUnit unit) {
            bean.callTimeout(timeout, unit);
            return this;
        }

        public Builder retryOnConnectionFailure(boolean retryOnConnectionFailure) {
            bean.retryOnConnectionFailure(retryOnConnectionFailure);
            return this;
        }

        public OidcClient build() {
            bean.addInterceptor(interceptor);
            return new OidcClient(bean.build(), baseUrl);
        }

    }

    // ------------------------------------------------------------------------

    public static class OidcInterceptor implements Interceptor {

        private OidcConfiguration configuration;

        private transient OidcToken token;

        @Override
        public Response intercept(Chain chain) throws IOException {
            String tokenText = access(chain).toString();
            LOGGER.debug(tokenText);
            Response response = chain.proceed(chain.request().newBuilder().addHeader(OidcConfiguration.KEY_AUTHORIZATION, tokenText).build());
            if (!response.isSuccessful()) token = null; // compulsory waste
            return response;
        }

        private OidcToken access(Chain chain) throws IOException {
            if (token != null && token.isAccessable()) return token;
            LOGGER.debug("access to oidc token endpoint...");
            FormBody.Builder form = new FormBody.Builder();
            form.add(OidcConfiguration.KEY_CLIENT_ID, configuration.getClientId());
            form.add(OidcConfiguration.KEY_CLIENT_SECRET, configuration.getClientSecret());
            form.add(OidcConfiguration.KEY_GRANT_TYPE, configuration.getGrantType().value());
            if (configuration.getScope() != null) form.add(OidcConfiguration.KEY_SCOPE, configuration.getScope());
            if (configuration.getGrantType() == GrantType.PASSWORD) {
                form.add(OidcConfiguration.KEY_USERNAME, configuration.getUsername());
                form.add(OidcConfiguration.KEY_PASSWORD, configuration.getPassword());
            }
            Request request = new Request.Builder().url(configuration.getEndpoint()).post(form.build()).build();
            try (Response response = chain.proceed(request)) {
                Preconditions.checkArgument(response.isSuccessful(), response.code());
                JsonNode body = MAPPER.readTree(response.body().byteStream());
                return token = OidcToken.of(body);
            }
        }

    }

}
