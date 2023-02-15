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

        private OidcInterceptor interceptor = new OidcInterceptor();

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
            return new OidcClient(bean.build());
        }

    }

    // ------------------------------------------------------------------------

    public static class OidcInterceptor implements Interceptor {

        private OidcConfiguration configuration;

        private transient OidcToken accessToken, refreshToken;

        @Override
        public Response intercept(Chain chain) throws IOException {
            String token = access(chain).toTypedString();
            Response response = chain.proceed(chain.request().newBuilder().addHeader(OidcConfiguration.KEY_AUTHORIZATION, token).build());
            if (!response.isSuccessful()) accessToken = null; // compulsory waste
            return response;
        }

        private OidcToken access(Chain chain) throws IOException {
            if (accessToken == null || accessToken.isExpired()) {
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
                authenticate(chain, form.build());
            }
            if (refreshToken != null && refreshToken.isExpired()) {
                LOGGER.debug("access to oidc refresh token ...");
                FormBody.Builder form = new FormBody.Builder();
                form.add(OidcConfiguration.KEY_CLIENT_ID, configuration.getClientId());
                form.add(OidcConfiguration.KEY_CLIENT_SECRET, configuration.getClientSecret());
                form.add(OidcConfiguration.KEY_GRANT_TYPE, GrantType.REFRESH_TOKEN.value());
                if (configuration.getScope() != null) form.add(OidcConfiguration.KEY_SCOPE, configuration.getScope());
                authenticate(chain, form.build());
            }
            return accessToken;
        }

        private void authenticate(Chain chain, FormBody form) throws IOException {
            Request request = new Request.Builder().url(configuration.getEndpoint()).post(form).build();
            try (Response response = chain.proceed(request)) {
                Preconditions.checkArgument(response.isSuccessful(), response.code());
                JsonNode body = MAPPER.readTree(response.body().byteStream());
                refreshToken = OidcToken.of(body.path(OidcConfiguration.KEY_REFRESH_TOKEN).textValue());
                accessToken = OidcToken.of(body.path(OidcConfiguration.KEY_ACCESS_TOKEN).textValue());
            }
        }

    }

}
