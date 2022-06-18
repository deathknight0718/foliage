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

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class RestApi {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerFactory.class);

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    static {
        MAPPER.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
    }

    // ------------------------------------------------------------------------

    public Response queryDeviceById(@QueryParam("id") String id) {
        return null;
    }

    public Response queryDevicesByPaging(@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
        return null;
    }

    public Response registerDevice(String json) {
        return null;
    }

    public Response queryGeographicsByDeviceId(@QueryParam("id") String id) {
        return null;
    }

}
