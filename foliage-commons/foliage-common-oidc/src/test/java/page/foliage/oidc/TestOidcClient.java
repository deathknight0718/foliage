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

import java.util.concurrent.TimeUnit;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import page.foliage.oidc.OidcConfiguration.GrantType;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestOidcClient {

    @Test
    public void testPassword() throws Exception {
        OidcConfiguration configuration = OidcConfiguration.builder() //
            .withEndpoint("https://auth.prod.greenstreet.cloud/realms/system") //
            .withClient("ngsi", "qBfBqKKUZ2RkE86LElM2BlMmJDYHGYRz") //
            .withGrantType(GrantType.PASSWORD) //
            .withCredential("deathknight0718@qq.com", "Zhouling93") //
            .build();
        OidcClient client = OidcClient.builder() //
            .oidc(configuration) //
            .retryOnConnectionFailure(false) //
            .callTimeout(30, TimeUnit.SECONDS) //
            .build();
        HttpUrl url = HttpUrl.get("https://gateway.prod.greenstreet.cloud/context/v2/entities");
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            System.err.println(response.body().string());
            System.err.println(response.code());
        }
    }

    @Test
    public void testClientCredentials() throws Exception {
        OidcConfiguration configuration = OidcConfiguration.builder() //
            .withEndpoint("https://auth.prod.greenstreet.cloud/realms/system") //
            .withClient("ngsi", "qBfBqKKUZ2RkE86LElM2BlMmJDYHGYRz") //
            .withGrantType(GrantType.CLIENT_CREDENTIALS) //
            .build();
        OidcClient client = OidcClient.builder() //
            .oidc(configuration) //
            .retryOnConnectionFailure(false) //
            .callTimeout(30, TimeUnit.SECONDS) //
            .build();
        HttpUrl url = HttpUrl.get("https://gateway.prod.greenstreet.cloud/context/v2/entities");
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            System.err.println(response.body().string());
            System.err.println(response.code());
        }
    }

}
