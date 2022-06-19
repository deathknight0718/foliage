/*
 * Copyright 2013 Deathknight0718.
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
package org.foliage.chinazdjs.rest;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.foliage.chinazdjs.api.Device;
import org.foliage.chinazdjs.util.ResponseUtils;
import org.foliage.guava.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Path("/api/v1")
public class RestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestApi.class);

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    static {
        MAPPER.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
    }

    @PUT
    @Path("/device/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerDevice(String json) {
        try {
            LOGGER.debug("called the method registerDevice with json text sizeof {}", json.length());
            JsonNode data = MAPPER.readTree(json);
            Device device = Device.of(data.get("devcode").asText(), data.get("name").asText());
            device.register();
            return ResponseUtils.handleSuccess(MAPPER.writeValueAsString(device));
        } catch (Exception e) {
            LOGGER.warn(Throwables.getStackTraceAsString(e));
            return ResponseUtils.handleException(e.getMessage());
        }
    }

    @GET
    @Path("/device/query-by-paging")
    public Response queryDevicesByPaging(@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
        try {
            LOGGER.debug("called the method queryDevicesByPaging with offset {} and limit {}", offset, limit);
            List<Device> devices = Device.listByPaging(offset, limit);
            return ResponseUtils.handleSuccess(MAPPER.writeValueAsString(devices));
        } catch (Exception e) {
            LOGGER.warn(Throwables.getStackTraceAsString(e));
            return ResponseUtils.handleException(e.getMessage());
        }
    }

    @GET
    @Path("/specification/{id}")
    public Response queryLatestSpecificationByDeviceId(@PathParam("id") String input) {
        try {
            LOGGER.debug("called the method queryLatestSpecificationByDeviceId with id {}", input);
            Device.Specification specification = Device.get(UUID.fromString(input)).latestSpecification();
            return ResponseUtils.handleSuccess(MAPPER.writeValueAsString(specification));
        } catch (Exception e) {
            LOGGER.warn(Throwables.getStackTraceAsString(e));
            return ResponseUtils.handleException(e.getMessage());
        }
    }

    @GET
    @Path("/geographic/{id}")
    public Response queryLatestGeographicByDeviceId(@PathParam("id") String input) {
        try {
            LOGGER.debug("called the method queryLatestGeographicByDeviceId with id {}", input);
            Device.Geographic geographic = Device.get(UUID.fromString(input)).latestGeographic();
            return ResponseUtils.handleSuccess(MAPPER.writeValueAsString(geographic));
        } catch (Exception e) {
            LOGGER.warn(Throwables.getStackTraceAsString(e));
            return ResponseUtils.handleException(e.getMessage());
        }
    }

}
