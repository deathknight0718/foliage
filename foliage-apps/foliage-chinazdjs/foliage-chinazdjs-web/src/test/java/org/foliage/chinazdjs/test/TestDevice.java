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
package org.foliage.chinazdjs.test;

import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;

import org.apache.commons.dbcp2.BasicDataSource;
import org.foliage.chinazdjs.api.Device;
import org.foliage.chinazdjs.api.impl.DeviceJdbcSession;
import org.foliage.chinazdjs.rest.RestApi;
import org.foliage.common.collect.Identities;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestDevice {

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @BeforeClass
    private static void beforeClass() throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.foliage.chinazdjs.mock.MockContextFactory");
        Context context = (Context) new InitialContext();
        Mockito.when(context.lookup("java:comp/env/jdbc/h2")).thenReturn(source());
        init();
    }

    private static void init() throws SQLException, Exception {
        try (DeviceJdbcSession session = new DeviceJdbcSession(source().getConnection())) {
            session.debugTables();
        }
    }

    private static DataSource source() {
        BasicDataSource source = new BasicDataSource();
        source.setDriver(org.h2.Driver.load());
        source.setUrl("jdbc:h2:/mnt/f6c7d3c2-5454-4fe2-96e5-08c0492b3ccb/project/foliage/foliage-apps/foliage-chinazdjs/foliage-chinazdjs-web/target/foliage-db");
        source.setUsername("foliage");
        source.setPassword("secret");
        return source;
    }

    @Test(enabled = false)
    public void testSession() throws Exception {
        Device.of("14893189421").register();
        Device device = Device.get("14893189421");
        Assert.assertEquals(device.getName(), "device-14893189421");
        System.err.println(device.latestGeographic());
        System.err.println(device.latestSpecification());
        Assert.assertEquals(Device.listByPaging(0, 10).size(), 1);
        System.err.println(Device.listByPaging(0, 10).iterator().next());
    }

    @Test(enabled = true)
    public void testRestApiRegisterDevice() throws Exception {
        ObjectNode post = MAPPER.createObjectNode();
        post.set("devcode", TextNode.valueOf("14811212136"));
        post.set("name", TextNode.valueOf("TEST-14811212136"));
        Response response = new RestApi().registerDevice(post.toString());
        Assert.assertEquals(response.getStatus(), 200);
    }

    @Test(enabled = true, dependsOnMethods = { "testRestApiRegisterDevice" })
    public void testRestApiQueryDevicesByPaging() throws Exception {
        Response response = new RestApi().queryDevicesByPaging(0, 10);
        Assert.assertEquals(response.getStatus(), 200);
        JsonNode json = MAPPER.readTree((String) response.getEntity());
        System.err.println(json);
    }

    @Test(enabled = true, dependsOnMethods = { "testRestApiRegisterDevice" })
    public void testRestApiQueryLatestSpecificationByDeviceId() throws Exception {
        Response response = new RestApi().queryLatestSpecificationByDeviceId(Identities.uuid("14811212136".getBytes()).toString());
        Assert.assertEquals(response.getStatus(), 200);
        JsonNode json = MAPPER.readTree((String) response.getEntity());
        System.err.println(json);
    }

    @Test(enabled = true, dependsOnMethods = { "testRestApiRegisterDevice" })
    public void testRestApiQueryLatestGeographicByDeviceId() throws Exception {
        Response response = new RestApi().queryLatestGeographicByDeviceId(Identities.uuid("14811212136".getBytes()).toString());
        Assert.assertEquals(response.getStatus(), 200);
        JsonNode json = MAPPER.readTree((String) response.getEntity());
        System.err.println(json);
    }

}
