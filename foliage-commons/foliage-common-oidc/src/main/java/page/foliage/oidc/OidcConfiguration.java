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

/**
 * 
 * @author deathknight0718@qq.com
 * @see RFC6749
 */
public class OidcConfiguration {

    // ------------------------------------------------------------------------

    public static final String KEY_AUTHORIZATION = "Authorization";
    
    public static final String KEY_SCOPE = "scope";
    
    public static final String KEY_CLIENT_ID = "client_id";

    public static final String KEY_CLIENT_SECRET = "client_secret";

    public static final String KEY_GRANT_TYPE = "grant_type";

    public static final String KEY_USERNAME = "username";

    public static final String KEY_PASSWORD = "password";

    public static final String KEY_REFRESH_TOKEN = "refresh_token";

    public static final String KEY_ACCESS_TOKEN = "access_token";

    // ------------------------------------------------------------------------

    private String endpoint;

    private String clientId, clientSecret;

    private String scope;

    private GrantType grantType;

    private String username, password;

    // ------------------------------------------------------------------------

    private OidcConfiguration() {}

    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------------

    public String getEndpoint() {
        return endpoint;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public GrantType getGrantType() {
        return grantType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // ------------------------------------------------------------------------

    public static class Builder {

        private OidcConfiguration bean = new OidcConfiguration();

        public Builder withEndpoint(String endpoint) {
            bean.endpoint = endpoint + "/protocol/openid-connect/token";
            return this;
        }

        public Builder withClient(String clientId, String clientSecret) {
            bean.clientId = clientId;
            bean.clientSecret = clientSecret;
            return this;
        }

        public Builder withGrantType(GrantType grantType) {
            bean.grantType = grantType;
            return this;
        }

        public Builder withCredential(String username, String password) {
            bean.username = username;
            bean.password = password;
            return this;
        }

        public OidcConfiguration build() {
            return bean;
        }

    }

    // ------------------------------------------------------------------------

    public static enum GrantType {

        PASSWORD("password"), //
        CLIENT_CREDENTIALS("client_credentials"), //
        REFRESH_TOKEN("refresh_token");

        private final String value;

        private GrantType(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

    }

}
