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

    private final static String HOST = "http://iot.scientop.com:7050";

    private final static ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private final ResteasyClient client;

    private final RestDeviceApi api;

    // ------------------------------------------------------------------------

    public DeviceRestSession() {
        this.client = new ResteasyClientBuilder().build();
        this.api = client.target(HOST).proxy(RestDeviceApi.class);
    }

    // ------------------------------------------------------------------------
    
    public JsonNode queryDeviceByDevcode(String devcode) throws IOException {
        ObjectNode post = MAPPER.createObjectNode();
        post.set("row", IntNode.valueOf(1));
        post.set("devcode", TextNode.valueOf(devcode));
        Response response = this.api.queryDeviceByDevcodes(MAPPER.writeValueAsString(post));
        try (InputStream is = (InputStream) response.getEntity()) {
            JsonNode rows = MAPPER.readTree(is).get("rows");
            Preconditions.checkArgument(!rows.isEmpty(), "该设备信息不存在！");
            return rows.get(0);
        }
    }

    public JsonNode queryGeographicByDevcode(String devcode) throws IOException {
        ObjectNode post = MAPPER.createObjectNode();
        post.set("devcodes", TextNode.valueOf(devcode));
        Response response = this.api.queryGeographicByDevcodes(MAPPER.writeValueAsString(post));
        try (InputStream is = (InputStream) response.getEntity()) {
            JsonNode data = MAPPER.readTree(is);
            return data.get(0);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        client.close();
    }

}
