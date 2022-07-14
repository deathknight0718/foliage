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
package org.foliage.chinazdjs.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Path("/")
public interface RestGeographicApi {

    @GET
    @Path("geocoding/v3/")
    Response geocoding(@QueryParam("ak") String accessKey, @QueryParam("output") String output, @QueryParam("address") String address);

    @GET
    @Path("reverse_geocoding/v3/")
    Response reverseGeocoding(@QueryParam("ak") String accessKey, @QueryParam("output") String output, @QueryParam("location") String location);

}
