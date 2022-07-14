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
package org.foliage.chinazdjs.api.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.foliage.chinazdjs.rest.RestDeviceApi;
import org.foliage.chinazdjs.rest.RestGeographicApi;
import org.foliage.guava.common.base.Preconditions;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class DeviceRestSession implements AutoCloseable {

    // ------------------------------------------------------------------------

    private final static String DEVICE_HOST = "http://iot.scientop.com:7050";

    private final static String GEOGRAPHIC_HOST = "https://api.map.baidu.com";

    private final static String GEOGRAPHIC_ACCESS_KEY = "9ReCHyoNrWzqCGqH5ETuxwQ9Fy4ARsdR";

    private final static String GEOGRAPHIC_FORMAT = "json";

    private final static ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private ResteasyClient deviceClient, geographicClient;

    private RestDeviceApi deviceApi;

    private RestGeographicApi geographicApi;

    // ------------------------------------------------------------------------

    public DeviceRestSession() {
        this.deviceClient = new ResteasyClientBuilder().build();
        this.deviceApi = deviceClient.target(DEVICE_HOST).proxy(RestDeviceApi.class);
        this.geographicClient = new ResteasyClientBuilder().build();
        this.geographicApi = deviceClient.target(GEOGRAPHIC_HOST).proxy(RestGeographicApi.class);
    }

    // ------------------------------------------------------------------------

    public JsonNode queryDeviceByDevcode(String devcode) throws IOException {
        ObjectNode post = MAPPER.createObjectNode();
        post.set("row", IntNode.valueOf(1));
        post.set("devcode", TextNode.valueOf(devcode));
        Response response = this.deviceApi.queryDeviceByDevcodes(MAPPER.writeValueAsString(post));
        try (InputStream is = (InputStream) response.getEntity()) {
            JsonNode rows = MAPPER.readTree(is).get("rows");
            Preconditions.checkArgument(!rows.isEmpty(), "该设备信息不存在！");
            return rows.get(0);
        }
    }

    public JsonNode queryGeographicByDevcode(String devcode) throws IOException {
        ObjectNode post = MAPPER.createObjectNode();
        post.set("devcodes", TextNode.valueOf(devcode));
        Response response1 = this.deviceApi.queryGeographicByDevcodes(MAPPER.writeValueAsString(post));
        try (InputStream is1 = (InputStream) response1.getEntity()) {
            JsonNode data = MAPPER.readTree(is1).get(0);
            Preconditions.checkArgument(!data.isNull());
            ObjectNode result = MAPPER.createObjectNode();
            result.set("geographic", data);
            String latitude = data.get("Lat").asText(), longitude = data.get("Lng").asText();
            Response response2 = geographicApi.reverseGeocoding(GEOGRAPHIC_ACCESS_KEY, GEOGRAPHIC_FORMAT, String.format("%s,%s", latitude, longitude));
            try (InputStream is2 = (InputStream) response2.getEntity()) {
                result.set("reverseGeocoding", MAPPER.readTree(is2));
            }
            return result;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        if (deviceClient != null && !deviceClient.isClosed()) deviceClient.close();
        if (geographicClient != null && !geographicClient.isClosed()) geographicClient.close();
    }

}
