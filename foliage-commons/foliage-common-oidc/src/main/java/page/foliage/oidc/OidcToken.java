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

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import page.foliage.common.util.CodecUtils;
import page.foliage.guava.common.base.Preconditions;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class OidcToken {

    // ------------------------------------------------------------------------

    private final static ObjectMapper MAPPER = new ObjectMapper();

    // ------------------------------------------------------------------------

    private JsonNode payload;

    private String base64Text;

    private Instant expiration;

    // ------------------------------------------------------------------------

    private OidcToken() {}

    // ------------------------------------------------------------------------

    public static OidcToken of(JsonNode body) {
        OidcToken token = new OidcToken();
        try {
            JsonNode node = body.path(OidcConfiguration.KEY_ACCESS_TOKEN);
            Preconditions.checkArgument(node.isTextual(), "Error! cannot found the access token.");
            token.base64Text = node.textValue();
            token.payload = MAPPER.readTree(CodecUtils.decodeBase64(StringUtils.split(token.base64Text, ".")[1]));
            token.expiration = Instant.ofEpochSecond(token.payload.path("exp").longValue() - 30);
            node = body.path(OidcConfiguration.KEY_ACCESS_EXPIRES_IN);
            if (node.isNumber()) token.expiration = Instant.now().plus(node.longValue() - 30, ChronoUnit.SECONDS);
            return token;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    public boolean isAccessable() {
        return expiration.isAfter(Instant.now());
    }

    @Override
    public String toString() {
        return payload.path("typ").textValue() + " " + base64Text;
    }

}
